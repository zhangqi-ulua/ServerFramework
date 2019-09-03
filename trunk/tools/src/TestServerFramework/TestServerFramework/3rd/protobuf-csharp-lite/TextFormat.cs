#region Copyright notice and license

// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// http://github.com/jskeet/dotnet-protobufs/
// Original C++/Java/Python code:
// http://code.google.com/p/protobuf/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#endregion
#pragma warning disable

using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text;
using Google.ProtocolBuffers.Descriptors;

namespace Google.ProtocolBuffers
{
    /// <summary>
    /// Provides ASCII text formatting support for messages.
    /// TODO(jonskeet): Support for alternative line endings.
    /// (Easy to print, via TextGenerator. Not sure about parsing.)
    /// </summary>
    public static class TextFormat
    {
        /// <summary>
        /// Outputs a textual representation of the Protocol Message supplied into
        /// the parameter output.
        /// </summary>
        public static void Print(IMessageLite message, TextWriter output)
        {
			message.WriteTo(CodedTextOutputStream.CreateInstance(new TextGenerator(output)));
        }

		public static string PrintToString(IMessageLite message)
        {
            StringWriter text = new StringWriter();
            Print(message, text);
            return text.ToString();
        }

        [CLSCompliant(false)]
        public static ulong ParseUInt64(string text)
        {
            return (ulong) ParseInteger(text, false, true);
        }

        public static long ParseInt64(string text)
        {
            return ParseInteger(text, true, true);
        }

        [CLSCompliant(false)]
        public static uint ParseUInt32(string text)
        {
            return (uint) ParseInteger(text, false, false);
        }

        public static int ParseInt32(string text)
        {
            return (int) ParseInteger(text, true, false);
        }

        public static float ParseFloat(string text)
        {
            switch (text)
            {
                case "-inf":
                case "-infinity":
                case "-inff":
                case "-infinityf":
                    return float.NegativeInfinity;
                case "inf":
                case "infinity":
                case "inff":
                case "infinityf":
                    return float.PositiveInfinity;
                case "nan":
                case "nanf":
                    return float.NaN;
                default:
                    return float.Parse(text, FrameworkPortability.InvariantCulture);
            }
        }

        public static double ParseDouble(string text)
        {
            switch (text)
            {
                case "-inf":
                case "-infinity":
                    return double.NegativeInfinity;
                case "inf":
                case "infinity":
                    return double.PositiveInfinity;
                case "nan":
                    return double.NaN;
                default:
                    return double.Parse(text, FrameworkPortability.InvariantCulture);
            }
        }

        /// <summary>
        /// Parses an integer in hex (leading 0x), decimal (no prefix) or octal (leading 0).
        /// Only a negative sign is permitted, and it must come before the radix indicator.
        /// </summary>
        private static long ParseInteger(string text, bool isSigned, bool isLong)
        {
            string original = text;
            bool negative = false;
            if (text.StartsWith("-"))
            {
                if (!isSigned)
                {
                    throw new FormatException("Number must be positive: " + original);
                }
                negative = true;
                text = text.Substring(1);
            }

            int radix = 10;
            if (text.StartsWith("0x"))
            {
                radix = 16;
                text = text.Substring(2);
            }
            else if (text.StartsWith("0"))
            {
                radix = 8;
            }

            ulong result;
            try
            {
                // Workaround for https://connect.microsoft.com/VisualStudio/feedback/ViewFeedback.aspx?FeedbackID=278448
                // We should be able to use Convert.ToUInt64 for all cases.
                result = radix == 10 ? ulong.Parse(text) : Convert.ToUInt64(text, radix);
            }
            catch (OverflowException)
            {
                // Convert OverflowException to FormatException so there's a single exception type this method can throw.
                string numberDescription = string.Format("{0}-bit {1}signed integer", isLong ? 64 : 32,
                                                         isSigned ? "" : "un");
                throw new FormatException("Number out of range for " + numberDescription + ": " + original);
            }

            if (negative)
            {
                ulong max = isLong ? 0x8000000000000000UL : 0x80000000L;
                if (result > max)
                {
                    string numberDescription = string.Format("{0}-bit signed integer", isLong ? 64 : 32);
                    throw new FormatException("Number out of range for " + numberDescription + ": " + original);
                }
                return -((long) result);
            }
            else
            {
                ulong max = isSigned
                                ? (isLong ? (ulong) long.MaxValue : int.MaxValue)
                                : (isLong ? ulong.MaxValue : uint.MaxValue);
                if (result > max)
                {
                    string numberDescription = string.Format("{0}-bit {1}signed integer", isLong ? 64 : 32,
                                                             isSigned ? "" : "un");
                    throw new FormatException("Number out of range for " + numberDescription + ": " + original);
                }
                return (long) result;
            }
        }

        /// <summary>
        /// Tests a character to see if it's an octal digit.
        /// </summary>
        private static bool IsOctal(char c)
        {
            return '0' <= c && c <= '7';
        }

        /// <summary>
        /// Tests a character to see if it's a hex digit.
        /// </summary>
        private static bool IsHex(char c)
        {
            return ('0' <= c && c <= '9') ||
                   ('a' <= c && c <= 'f') ||
                   ('A' <= c && c <= 'F');
        }

        /// <summary>
        /// Interprets a character as a digit (in any base up to 36) and returns the
        /// numeric value.
        /// </summary>
        private static int ParseDigit(char c)
        {
            if ('0' <= c && c <= '9')
            {
                return c - '0';
            }
            else if ('a' <= c && c <= 'z')
            {
                return c - 'a' + 10;
            }
            else
            {
                return c - 'A' + 10;
            }
        }

        /// <summary>
        /// Unescapes a text string as escaped using <see cref="EscapeText(string)" />.
        /// Two-digit hex escapes (starting with "\x" are also recognised.
        /// </summary>
        public static string UnescapeText(string input)
        {
			System.Text.StringBuilder result = new StringBuilder();
            for (int i = 0; i < input.Length; i++)
            {
                char c = input[i];
                if (c != '\\')
                {
					result.Append(c);
                    continue;
                }
                if (i + 1 >= input.Length)
                {
                    throw new FormatException("Invalid escape sequence: '\\' at end of string.");
                }

                i++;
                c = input[i];
                if (c >= '0' && c <= '7')
                {
                    // Octal escape. 
                    int code = ParseDigit(c);
                    if (i + 1 < input.Length && IsOctal(input[i + 1]))
                    {
                        i++;
                        code = code*8 + ParseDigit(input[i]);
                    }
                    if (i + 1 < input.Length && IsOctal(input[i + 1]))
                    {
                        i++;
                        code = code*8 + ParseDigit(input[i]);
                    }
					result.Append(c);
                }
                else
                {
                    switch (c)
                    {
                        case 'a':
                            result.Append((char) 0x07);
                            break;
                        case 'b':
                            result.Append((char) '\b');
                            break;
                        case 'f':
                            result.Append((char) '\f');
                            break;
                        case 'n':
                            result.Append((char) '\n');
                            break;
                        case 'r':
                            result.Append((char) '\r');
                            break;
                        case 't':
                            result.Append((char) '\t');
                            break;
                        case 'v':
                            result.Append((char) 0x0b);
                            break;
                        case '\\':
                            result.Append((char) '\\');
                            break;
                        case '\'':
                            result.Append((char) '\'');
                            break;
                        case '"':
                            result.Append((char) '\"');
                            break;

                        case 'x':
                            // hex escape
                            int code;
                            if (i + 1 < input.Length && IsHex(input[i + 1]))
                            {
                                i++;
                                code = ParseDigit(input[i]);
                            }
                            else
                            {
                                throw new FormatException("Invalid escape sequence: '\\x' with no digits");
                            }
                            if (i + 1 < input.Length && IsHex(input[i + 1]))
                            {
                                ++i;
                                code = code*16 + ParseDigit(input[i]);
                            }
                            result.Append((char) code);
                            break;

                        default:
                            throw new FormatException("Invalid escape sequence: '\\" + c + "'");
                    }
                }
            }

			return result.ToString();
        }

        /// <summary>
        /// Like <see cref="EscapeBytes" /> but escapes a text string.
        /// The string is first encoded as UTF-8, then each byte escaped individually.
        /// The returned value is guaranteed to be entirely ASCII.
        /// </summary>
        public static string EscapeText(string input)
        {
            return EscapeBytes(ByteString.CopyFromUtf8(input));
        }

        /// <summary>
        /// Escapes bytes in the format used in protocol buffer text format, which
        /// is the same as the format used for C string literals.  All bytes
        /// that are not printable 7-bit ASCII characters are escaped, as well as
        /// backslash, single-quote, and double-quote characters.  Characters for
        /// which no defined short-hand escape sequence is defined will be escaped
        /// using 3-digit octal sequences.
        /// The returned value is guaranteed to be entirely ASCII.
        /// </summary>
        public static String EscapeBytes(ByteString input)
        {
            StringBuilder builder = new StringBuilder(input.Length);
            foreach (byte b in input)
            {
                switch (b)
                {
                        // C# does not use \a or \v
                    case 0x07:
                        builder.Append("\\a");
                        break;
                    case (byte) '\b':
                        builder.Append("\\b");
                        break;
                    case (byte) '\f':
                        builder.Append("\\f");
                        break;
                    case (byte) '\n':
                        builder.Append("\\n");
                        break;
                    case (byte) '\r':
                        builder.Append("\\r");
                        break;
                    case (byte) '\t':
                        builder.Append("\\t");
                        break;
                    case 0x0b:
                        builder.Append("\\v");
                        break;
                    case (byte) '\\':
                        builder.Append("\\\\");
                        break;
                    case (byte) '\'':
                        builder.Append("\\\'");
                        break;
                    case (byte) '"':
                        builder.Append("\\\"");
                        break;
                    default:
                        if (b >= 0x20 && b < 128)
                        {
                            builder.Append((char) b);
                        }
                        else
                        {
                            builder.Append('\\');
                            builder.Append((char) ('0' + ((b >> 6) & 3)));
                            builder.Append((char) ('0' + ((b >> 3) & 7)));
                            builder.Append((char) ('0' + (b & 7)));
                        }
                        break;
                }
            }
            return builder.ToString();
        }

        /// <summary>
        /// Performs string unescaping from C style (octal, hex, form feeds, tab etc) into a byte string.
        /// </summary>
        public static ByteString UnescapeBytes(string input)
        {
            byte[] result = new byte[input.Length];
            int pos = 0;
            for (int i = 0; i < input.Length; i++)
            {
                char c = input[i];
				if (c > 127 || c < 32)
				{
					throw new FormatException("Escaped string must only contain ASCII");
				}
                if (c != '\\')
                {
                    result[pos++] = (byte) c;
                    continue;
                }
                if (i + 1 >= input.Length)
                {
                    throw new FormatException("Invalid escape sequence: '\\' at end of string.");
                }

                i++;
                c = input[i];
                if (c >= '0' && c <= '7')
                {
                    // Octal escape. 
                    int code = ParseDigit(c);
                    if (i + 1 < input.Length && IsOctal(input[i + 1]))
                    {
                        i++;
                        code = code*8 + ParseDigit(input[i]);
                    }
                    if (i + 1 < input.Length && IsOctal(input[i + 1]))
                    {
                        i++;
                        code = code*8 + ParseDigit(input[i]);
                    }
                    result[pos++] = (byte) code;
                }
                else
                {
                    switch (c)
                    {
                        case 'a':
                            result[pos++] = 0x07;
                            break;
                        case 'b':
                            result[pos++] = (byte) '\b';
                            break;
                        case 'f':
                            result[pos++] = (byte) '\f';
                            break;
                        case 'n':
                            result[pos++] = (byte) '\n';
                            break;
                        case 'r':
                            result[pos++] = (byte) '\r';
                            break;
                        case 't':
                            result[pos++] = (byte) '\t';
                            break;
                        case 'v':
                            result[pos++] = 0x0b;
                            break;
                        case '\\':
                            result[pos++] = (byte) '\\';
                            break;
                        case '\'':
                            result[pos++] = (byte) '\'';
                            break;
                        case '"':
                            result[pos++] = (byte) '\"';
                            break;

                        case 'x':
                            // hex escape
                            int code;
                            if (i + 1 < input.Length && IsHex(input[i + 1]))
                            {
                                i++;
                                code = ParseDigit(input[i]);
                            }
                            else
                            {
                                throw new FormatException("Invalid escape sequence: '\\x' with no digits");
                            }
                            if (i + 1 < input.Length && IsHex(input[i + 1]))
                            {
                                ++i;
                                code = code*16 + ParseDigit(input[i]);
                            }
                            result[pos++] = (byte) code;
                            break;

                        default:
                            throw new FormatException("Invalid escape sequence: '\\" + c + "'");
                    }
                }
            }

            return ByteString.CopyFrom(result, 0, pos);
        }

        public static void Merge(string text, IBuilderLite builder)
        {
            Merge(text, ExtensionRegistry.Empty, builder);
        }

        public static void Merge(TextReader reader, IBuilderLite builder)
        {
            Merge(reader, ExtensionRegistry.Empty, builder);
        }

        public static void Merge(TextReader reader, ExtensionRegistry registry, IBuilderLite builder)
        {
            Merge(reader.ReadToEnd(), registry, builder);
        }

        public static void Merge(string text, ExtensionRegistry registry, IBuilderLite builder)
        {
			TextTokenizer tokenizer = new TextTokenizer(text);
			TextInputStream ts = TextInputStream.CreateInstance(tokenizer);
			builder.WeakMergeFrom(ts);
        }
    }
}
#pragma warning restore
