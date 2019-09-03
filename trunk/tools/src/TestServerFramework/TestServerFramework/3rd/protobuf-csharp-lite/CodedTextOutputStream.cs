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
using System.IO;
using System.Text;
using Google.ProtocolBuffers.Collections;
using Google.ProtocolBuffers.Descriptors;

namespace Google.ProtocolBuffers
{
    /// <summary>
    /// Encodes and writes protocol message fields.
    /// </summary>
    /// <remarks>
    /// This class contains two kinds of methods:  methods that write specific
    /// protocol message constructs and field types (e.g. WriteTag and
    /// WriteInt32) and methods that write low-level values (e.g.
    /// WriteRawVarint32 and WriteRawBytes).  If you are writing encoded protocol
    /// messages, you should use the former methods, but if you are writing some
    /// other format of your own design, use the latter. The names of the former
    /// methods are taken from the protocol buffer type names, not .NET types.
    /// (Hence WriteFloat instead of WriteSingle, and WriteBool instead of WriteBoolean.)
    /// </remarks>
    public sealed partial class CodedTextOutputStream : ICodedOutputStream
    {
		private readonly TextGenerator output;

        #region Construction

		private CodedTextOutputStream(TextGenerator output)
        {
            this.output = output;
        }

        /// <summary>
        /// Creates a new CodedOutputStream which write to the given stream and uses
        /// the specified buffer size.
        /// </summary>
		public static CodedTextOutputStream CreateInstance(TextGenerator output)
        {
			return new CodedTextOutputStream(output);
        }

        #endregion
        
        void ICodedOutputStream.WriteMessageStart() { }
        void ICodedOutputStream.WriteMessageEnd() { Flush(); }

        #region Writing of unknown fields

        public void WriteUnknownBytes(int fieldNumber, ByteString value)
        {
            WriteBytes(fieldNumber, null /*not used*/, value);
        }

        [CLSCompliant(false)]
        public void WriteUnknownField(int fieldNumber, WireFormat.WireType wireType, ulong value)
        {
            if (wireType == WireFormat.WireType.Varint)
            {
                WriteUInt64(fieldNumber, null /*not used*/, value);
            }
            else if (wireType == WireFormat.WireType.Fixed32)
            {
                WriteFixed32(fieldNumber, null /*not used*/, (uint) value);
            }
            else if (wireType == WireFormat.WireType.Fixed64)
            {
                WriteFixed64(fieldNumber, null /*not used*/, value);
            }
            else
            {
                throw InvalidProtocolBufferException.InvalidWireType();
            }
        }

        #endregion

        #region Writing of tags and fields

        public void WriteField(FieldType fieldType, int fieldNumber, string fieldName, object value)
        {
            switch (fieldType)
            {
                case FieldType.String:
                    WriteString(fieldNumber, fieldName, (string) value);
                    break;
                case FieldType.Message:
                    WriteMessage(fieldNumber, fieldName, (IMessageLite) value);
                    break;
                case FieldType.Group:
                    WriteGroup(fieldNumber, fieldName, (IMessageLite) value);
                    break;
                case FieldType.Bytes:
                    WriteBytes(fieldNumber, fieldName, (ByteString) value);
                    break;
                case FieldType.Bool:
                    WriteBool(fieldNumber, fieldName, (bool) value);
                    break;
                case FieldType.Enum:
                    if (value is Enum)
                    {
                        WriteEnum(fieldNumber, fieldName, (int) value, null /*not used*/);
                    }
                    else
                    {
                        WriteEnum(fieldNumber, fieldName, ((IEnumLite) value).Number, null /*not used*/);
                    }
                    break;
                case FieldType.Int32:
                    WriteInt32(fieldNumber, fieldName, (int) value);
                    break;
                case FieldType.Int64:
                    WriteInt64(fieldNumber, fieldName, (long) value);
                    break;
                case FieldType.UInt32:
                    WriteUInt32(fieldNumber, fieldName, (uint) value);
                    break;
                case FieldType.UInt64:
                    WriteUInt64(fieldNumber, fieldName, (ulong) value);
                    break;
                case FieldType.SInt32:
                    WriteSInt32(fieldNumber, fieldName, (int) value);
                    break;
                case FieldType.SInt64:
                    WriteSInt64(fieldNumber, fieldName, (long) value);
                    break;
                case FieldType.Fixed32:
                    WriteFixed32(fieldNumber, fieldName, (uint) value);
                    break;
                case FieldType.Fixed64:
                    WriteFixed64(fieldNumber, fieldName, (ulong) value);
                    break;
                case FieldType.SFixed32:
                    WriteSFixed32(fieldNumber, fieldName, (int) value);
                    break;
                case FieldType.SFixed64:
                    WriteSFixed64(fieldNumber, fieldName, (long) value);
                    break;
                case FieldType.Double:
                    WriteDouble(fieldNumber, fieldName, (double) value);
                    break;
                case FieldType.Float:
                    WriteFloat(fieldNumber, fieldName, (float) value);
                    break;
            }
        }

        /// <summary>
        /// Writes a double field value, including tag, to the stream.
        /// </summary>
        public void WriteDouble(int fieldNumber, string fieldName, double value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes a float field value, including tag, to the stream.
        /// </summary>
        public void WriteFloat(int fieldNumber, string fieldName, float value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes a uint64 field value, including tag, to the stream.
        /// </summary>
        [CLSCompliant(false)]
        public void WriteUInt64(int fieldNumber, string fieldName, ulong value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes an int64 field value, including tag, to the stream.
        /// </summary>
        public void WriteInt64(int fieldNumber, string fieldName, long value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes an int32 field value, including tag, to the stream.
        /// </summary>
        public void WriteInt32(int fieldNumber, string fieldName, int value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes a fixed64 field value, including tag, to the stream.
        /// </summary>
        [CLSCompliant(false)]
        public void WriteFixed64(int fieldNumber, string fieldName, ulong value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes a fixed32 field value, including tag, to the stream.
        /// </summary>
        [CLSCompliant(false)]
        public void WriteFixed32(int fieldNumber, string fieldName, uint value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes a bool field value, including tag, to the stream.
        /// </summary>
        public void WriteBool(int fieldNumber, string fieldName, bool value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        /// <summary>
        /// Writes a string field value, including tag, to the stream.
        /// </summary>
        public void WriteString(int fieldNumber, string fieldName, string value)
        {
			output.WriteLine("{0}: \"{1}\"", fieldName, TextFormat.EscapeText(value));
        }

        /// <summary>
        /// Writes a group field value, including tag, to the stream.
        /// </summary>
        public void WriteGroup(int fieldNumber, string fieldName, IMessageLite value)
        {
			output.WriteLine("{0}: {{", fieldName);
			output.Indent();
			value.WriteTo(this);
			output.Outdent();
			output.WriteLine("}");
        }

        public void WriteMessage(int fieldNumber, string fieldName, IMessageLite value)
        {
			output.WriteLine("{0}: {{", fieldName);
			output.Indent();
			value.WriteTo(this);
			output.Outdent();
			output.WriteLine("}");
        }

        public void WriteBytes(int fieldNumber, string fieldName, ByteString value)
        {
			output.WriteLine("{0}: \"{1}\"", fieldName, TextFormat.EscapeBytes(value));
        }

        [CLSCompliant(false)]
        public void WriteUInt32(int fieldNumber, string fieldName, uint value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        public void WriteEnum(int fieldNumber, string fieldName, int value, object rawValue)
        {
			output.WriteLine("{0}: {1}", fieldName, rawValue);
        }

        public void WriteSFixed32(int fieldNumber, string fieldName, int value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        public void WriteSFixed64(int fieldNumber, string fieldName, long value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        public void WriteSInt32(int fieldNumber, string fieldName, int value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        public void WriteSInt64(int fieldNumber, string fieldName, long value)
        {
			output.WriteLine("{0}: {1}", fieldName, value);
        }

        public void WriteMessageSetExtension(int fieldNumber, string fieldName, IMessageLite value)
        {
        }

        public void WriteMessageSetExtension(int fieldNumber, string fieldName, ByteString value)
        {
        }

        #endregion

        #region Write array members

        public void WriteArray(FieldType fieldType, int fieldNumber, string fieldName, IEnumerable list)
        {
			output.WriteLine("{0}: [", fieldName);
			output.Indent();

			int objcount = 0;
			foreach (object element in list)
			{
				if (objcount != 0)
					output.Write(" ");

				switch (fieldType)
				{
					case FieldType.String:
						output.WriteLine("\"{0}\"", TextFormat.EscapeText((string)element));
						break;
					case FieldType.Bytes:
						output.WriteLine("\"{0}\"", TextFormat.EscapeBytes((ByteString)element));
						break;
					case FieldType.Message:
						output.WriteLine("{");
						output.Indent();
						((IMessageLite)element).WriteTo(this);
						output.Outdent();
						output.WriteLine("}");
						break;
					case FieldType.Group:
						output.WriteLine("{");
						output.Indent();
						((IMessageLite)element).WriteTo(this);
						output.Outdent();
						output.WriteLine("}");
						break;
					default:
						output.Write("{0}", element);
						if (++objcount > 10)
						{
							objcount = 0;
							output.WriteLine("");
						}
						break;
				}
			}
			if (objcount != 0)
				output.WriteLine("");
			output.Outdent();
			output.WriteLine("]");
        }

        public void WriteGroupArray<T>(int fieldNumber, string fieldName, IEnumerable<T> list)
            where T : IMessageLite
        {
			WriteArray(FieldType.Group, fieldNumber, fieldName, list);
        }

        public void WriteMessageArray<T>(int fieldNumber, string fieldName, IEnumerable<T> list)
            where T : IMessageLite
        {
			WriteArray(FieldType.Message, fieldNumber, fieldName, list);
        }

        public void WriteStringArray(int fieldNumber, string fieldName, IEnumerable<string> list)
        {
			WriteArray(FieldType.String, fieldNumber, fieldName, list);
        }

        public void WriteBytesArray(int fieldNumber, string fieldName, IEnumerable<ByteString> list)
        {
			WriteArray(FieldType.Bytes, fieldNumber, fieldName, list);
        }

        public void WriteBoolArray(int fieldNumber, string fieldName, IEnumerable<bool> list)
        {
			WriteArray(FieldType.Bool, fieldNumber, fieldName, list);
        }

        public void WriteInt32Array(int fieldNumber, string fieldName, IEnumerable<int> list)
        {
			WriteArray(FieldType.Int32, fieldNumber, fieldName, list);
        }

        public void WriteSInt32Array(int fieldNumber, string fieldName, IEnumerable<int> list)
        {
			WriteArray(FieldType.SInt32, fieldNumber, fieldName, list);
        }

        public void WriteUInt32Array(int fieldNumber, string fieldName, IEnumerable<uint> list)
        {
			WriteArray(FieldType.UInt32, fieldNumber, fieldName, list);
        }

        public void WriteFixed32Array(int fieldNumber, string fieldName, IEnumerable<uint> list)
        {
			WriteArray(FieldType.Fixed32, fieldNumber, fieldName, list);
        }

        public void WriteSFixed32Array(int fieldNumber, string fieldName, IEnumerable<int> list)
        {
			WriteArray(FieldType.SFixed32, fieldNumber, fieldName, list);
        }

        public void WriteInt64Array(int fieldNumber, string fieldName, IEnumerable<long> list)
        {
			WriteArray(FieldType.Int64, fieldNumber, fieldName, list);
        }

        public void WriteSInt64Array(int fieldNumber, string fieldName, IEnumerable<long> list)
        {
			WriteArray(FieldType.SInt64, fieldNumber, fieldName, list);
        }

        public void WriteUInt64Array(int fieldNumber, string fieldName, IEnumerable<ulong> list)
        {
			WriteArray(FieldType.UInt64, fieldNumber, fieldName, list);
        }

        public void WriteFixed64Array(int fieldNumber, string fieldName, IEnumerable<ulong> list)
        {
			WriteArray(FieldType.Fixed64, fieldNumber, fieldName, list);
        }

        public void WriteSFixed64Array(int fieldNumber, string fieldName, IEnumerable<long> list)
        {
			WriteArray(FieldType.SFixed64, fieldNumber, fieldName, list);
        }

        public void WriteDoubleArray(int fieldNumber, string fieldName, IEnumerable<double> list)
        {
			WriteArray(FieldType.Double, fieldNumber, fieldName, list);
        }

        public void WriteFloatArray(int fieldNumber, string fieldName, IEnumerable<float> list)
        {
			WriteArray(FieldType.Float, fieldNumber, fieldName, list);
        }

        [CLSCompliant(false)]
        public void WriteEnumArray<T>(int fieldNumber, string fieldName, IEnumerable<T> list)
            where T : struct, IComparable, IFormattable
        {
			WriteArray(FieldType.Enum, fieldNumber, fieldName, list);
        }

        #endregion

        #region Write packed array members

        public void WritePackedArray(FieldType fieldType, int fieldNumber, string fieldName, IEnumerable list)
        {
			WriteArray(fieldType, fieldNumber, fieldName, list);
        }

        public void WritePackedGroupArray<T>(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<T> list)
            where T : IMessageLite
        {
			WriteArray(FieldType.Group, fieldNumber, fieldName, list);
        }

        public void WritePackedMessageArray<T>(int fieldNumber, string fieldName, int calculatedSize,
                                               IEnumerable<T> list)
            where T : IMessageLite
        {
			WriteArray(FieldType.Message, fieldNumber, fieldName, list);
        }

        public void WritePackedStringArray(int fieldNumber, string fieldName, int calculatedSize,
                                           IEnumerable<string> list)
        {
			WriteArray(FieldType.String, fieldNumber, fieldName, list);
        }

        public void WritePackedBytesArray(int fieldNumber, string fieldName, int calculatedSize,
                                          IEnumerable<ByteString> list)
        {
			WriteArray(FieldType.Bytes, fieldNumber, fieldName, list);
        }

        public void WritePackedBoolArray(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<bool> list)
        {
			WriteArray(FieldType.Bool, fieldNumber, fieldName, list);
        }

        public void WritePackedInt32Array(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<int> list)
        {
			WriteArray(FieldType.Int32, fieldNumber, fieldName, list);
        }

        public void WritePackedSInt32Array(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<int> list)
        {
			WriteArray(FieldType.SInt32, fieldNumber, fieldName, list);
        }

        public void WritePackedUInt32Array(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<uint> list)
        {
			WriteArray(FieldType.UInt32, fieldNumber, fieldName, list);
        }

        public void WritePackedFixed32Array(int fieldNumber, string fieldName, int calculatedSize,
                                            IEnumerable<uint> list)
        {
			WriteArray(FieldType.Fixed32, fieldNumber, fieldName, list);
        }

        public void WritePackedSFixed32Array(int fieldNumber, string fieldName, int calculatedSize,
                                             IEnumerable<int> list)
        {
			WriteArray(FieldType.SFixed32, fieldNumber, fieldName, list);
        }

        public void WritePackedInt64Array(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<long> list)
        {
			WriteArray(FieldType.Int64, fieldNumber, fieldName, list);
        }

        public void WritePackedSInt64Array(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<long> list)
        {
			WriteArray(FieldType.SInt64, fieldNumber, fieldName, list);
        }

        public void WritePackedUInt64Array(int fieldNumber, string fieldName, int calculatedSize,
                                           IEnumerable<ulong> list)
        {
			WriteArray(FieldType.UInt64, fieldNumber, fieldName, list);
        }

        public void WritePackedFixed64Array(int fieldNumber, string fieldName, int calculatedSize,
                                            IEnumerable<ulong> list)
        {
			WriteArray(FieldType.Fixed64, fieldNumber, fieldName, list);
        }

        public void WritePackedSFixed64Array(int fieldNumber, string fieldName, int calculatedSize,
                                             IEnumerable<long> list)
        {
			WriteArray(FieldType.SFixed64, fieldNumber, fieldName, list);
        }

        public void WritePackedDoubleArray(int fieldNumber, string fieldName, int calculatedSize,
                                           IEnumerable<double> list)
        {
			WriteArray(FieldType.Double, fieldNumber, fieldName, list);
        }

        public void WritePackedFloatArray(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<float> list)
        {
			WriteArray(FieldType.Float, fieldNumber, fieldName, list);
        }

        [CLSCompliant(false)]
        public void WritePackedEnumArray<T>(int fieldNumber, string fieldName, int calculatedSize, IEnumerable<T> list)
            where T : struct, IComparable, IFormattable
        {
			WriteArray(FieldType.Enum, fieldNumber, fieldName, list);
        }

        #endregion

        public void Flush()
        {
            if (output != null)
            {
            }
        }
    }
}
#pragma warning restore
