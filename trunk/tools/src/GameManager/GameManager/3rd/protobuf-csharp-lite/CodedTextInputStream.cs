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
using System.Collections.Generic;
using System.IO;
using System.Text;
using Google.ProtocolBuffers.Descriptors;

namespace Google.ProtocolBuffers
{
    /// <summary>
    /// Readings and decodes protocol message fields.
    /// </summary>
    /// <remarks>
    /// This class contains two kinds of methods:  methods that read specific
    /// protocol message constructs and field types (e.g. ReadTag and
    /// ReadInt32) and methods that read low-level values (e.g.
    /// ReadRawVarint32 and ReadRawBytes).  If you are reading encoded protocol
    /// messages, you should use the former methods, but if you are reading some
    /// other format of your own design, use the latter. The names of the former
    /// methods are taken from the protocol buffer type names, not .NET types.
    /// (Hence ReadFloat instead of ReadSingle, and ReadBool instead of ReadBoolean.)
    /// 
    /// TODO(jonskeet): Consider whether recursion and size limits shouldn't be readonly,
    /// set at construction time.
    /// </remarks>
    public sealed class TextInputStream : ICodedInputStream
    {
        private string lastTag = null;

        private string nextTag = null;
        private bool hasNextTag = false;

		private TextTokenizer tokenizer;

        /// <summary>
        /// <see cref="SetRecursionLimit"/>
        /// </summary>
        private int recursionDepth = 0;
		private int recursionLimit = 64;


        #region Construction

        /// <summary>
        /// Creates a new TextInputStream reading data from the given
        /// stream.
        /// </summary>
		public static TextInputStream CreateInstance(TextTokenizer input)
        {
			return new TextInputStream(input);
        }

		private TextInputStream(TextTokenizer input)
        {
			this.tokenizer = input;
        }
        #endregion

        void ICodedInputStream.ReadMessageStart() { }
        void ICodedInputStream.ReadMessageEnd() { }

        #region Validation

        /// <summary>
        /// Verifies that the last call to ReadTag() returned the given tag value.
        /// This is used to verify that a nested group ended with the correct
        /// end tag.
        /// </summary>
        /// <exception cref="InvalidProtocolBufferException">The last
        /// tag read was not the one specified</exception>
        [CLSCompliant(false)]
        public void CheckLastTagWas(string value)
        {
            if (lastTag != value)
            {
                throw InvalidProtocolBufferException.InvalidEndTag();
            }
        }

        #endregion

        #region Reading of tags etc

        /// <summary>
        /// Attempt to peek at the next field tag.
        /// </summary>
        [CLSCompliant(false)]
        public bool PeekNextTag(out uint fieldTag, out string fieldName)
        {
            if (hasNextTag)
            {
                fieldName = nextTag;
                fieldTag = 0;
                return true;
            }

            string savedLast = lastTag;
			hasNextTag = ReadTag(out fieldTag, out nextTag);
            lastTag = savedLast;
			fieldName = nextTag;
            return hasNextTag;
        }

        /// <summary>
        /// Attempt to read a field tag, returning false if we have reached the end
        /// of the input data.
        /// </summary>
        /// <param name="fieldTag">The 'tag' of the field (id * 8 + wire-format)</param>
        /// <param name="fieldName">Not Supported - For protobuffer streams, this parameter is always null</param>
        /// <returns>true if the next fieldTag was read</returns>
        [CLSCompliant(false)]
        public bool ReadTag(out uint fieldTag, out string fieldName)
        {
			fieldTag = 0;
            if (hasNextTag)
            {
				fieldName = nextTag;
				lastTag = fieldName;
                hasNextTag = false;
                return true;
            }

            if (IsAtEnd)
            {
				fieldName = null;
				lastTag = fieldName;
                return false;
            }

            if (tokenizer.TryConsume("}"))
			{
				fieldName = null;
				lastTag = "}";
                return false;
            }

            if (tokenizer.TryConsume(">"))
			{
				fieldName = null;
				lastTag = ">";
                return false;
            }

			fieldName = tokenizer.ConsumeIdentifier();
			lastTag = fieldName;
            if (lastTag == null)
            {
                // If we actually read zero, that's not a valid tag.
                throw InvalidProtocolBufferException.InvalidTag();
            }
            return true;
        }

        /// <summary>
        /// Read a double field from the stream.
        /// </summary>
        public bool ReadDouble(ref double value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeDouble();
            return true;
        }

        /// <summary>
        /// Read a float field from the stream.
        /// </summary>
        public bool ReadFloat(ref float value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeFloat();
            return true;
        }

        /// <summary>
        /// Read a uint64 field from the stream.
        /// </summary>
        [CLSCompliant(false)]
        public bool ReadUInt64(ref ulong value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeUInt64();
            return true;
        }

        /// <summary>
        /// Read an int64 field from the stream.
        /// </summary>
        public bool ReadInt64(ref long value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeInt64();
            return true;
        }

        /// <summary>
        /// Read an int32 field from the stream.
        /// </summary>
        public bool ReadInt32(ref int value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeInt32();
            return true;
        }

        /// <summary>
        /// Read a fixed64 field from the stream.
        /// </summary>
        [CLSCompliant(false)]
        public bool ReadFixed64(ref ulong value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeUInt64();
            return true;
        }

        /// <summary>
        /// Read a fixed32 field from the stream.
        /// </summary>
        [CLSCompliant(false)]
        public bool ReadFixed32(ref uint value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeUInt32();
            return true;
        }

        /// <summary>
        /// Read a bool field from the stream.
        /// </summary>
        public bool ReadBool(ref bool value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeBoolean();
            return true;
        }

        /// <summary>
        /// Reads a string field from the stream.
        /// </summary>
        public bool ReadString(ref string value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeString();
            return true;
        }

        /// <summary>
        /// Reads a group field value from the stream.
        /// </summary>    
        public void ReadGroup(int fieldNumber, IBuilderLite builder,
                              ExtensionRegistry extensionRegistry)
        {
            if (recursionDepth >= recursionLimit)
            {
                throw InvalidProtocolBufferException.RecursionLimitExceeded();
            }
            ++recursionDepth;
			tokenizer.TryConsume(":");
			string endToken = "}";
			if (!tokenizer.TryConsume("{"))
			{
				endToken = ">";
				tokenizer.Consume("<");
			}
            builder.WeakMergeFrom(this, extensionRegistry);
			CheckLastTagWas(endToken);
            --recursionDepth;
        }

        /// <summary>
        /// Reads a group field value from the stream and merges it into the given
        /// UnknownFieldSet.
        /// </summary>   
        [Obsolete]
        public void ReadUnknownGroup(int fieldNumber, IBuilderLite builder)
        {
            if (recursionDepth >= recursionLimit)
            {
                throw InvalidProtocolBufferException.RecursionLimitExceeded();
            }
            ++recursionDepth;
            builder.WeakMergeFrom(this);
			//CheckLastTagWas(WireFormat.MakeTag(fieldNumber, WireFormat.WireType.EndGroup));
            --recursionDepth;
        }

        /// <summary>
        /// Reads an embedded message field value from the stream.
        /// </summary>   
        public void ReadMessage(IBuilderLite builder, ExtensionRegistry extensionRegistry)
        {
            if (++recursionDepth >= recursionLimit)
                throw InvalidProtocolBufferException.RecursionLimitExceeded();
			tokenizer.TryConsume(":");
			tokenizer.TryConsume("{");
            builder.WeakMergeFrom(this, extensionRegistry);
			CheckLastTagWas("}");
            --recursionDepth;
        }

        /// <summary>
        /// Reads a bytes field value from the stream.
        /// </summary>   
        public bool ReadBytes(ref ByteString value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeByteString();
			return true;
        }

        /// <summary>
        /// Reads a uint32 field value from the stream.
        /// </summary>   
        public bool ReadUInt32(ref uint value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeUInt32();
            return true;
        }

        /// <summary>
        /// Reads an enum field value from the stream. The caller is responsible
        /// for converting the numeric value to an actual enum.
        /// </summary>   
        public bool ReadEnum(ref IEnumLite value, out object unknown, IEnumLiteMap mapping)
        {
			tokenizer.Consume(":");
			if (tokenizer.LookingAtInteger())
			{
				int rawValue = tokenizer.ConsumeInt32();
				value = mapping.FindValueByNumber(rawValue);
				if (value != null)
				{
					unknown = null;
					return true;
				}
				unknown = rawValue;
			}
			else
			{
				string rawName = tokenizer.ConsumeIdentifier();
				value = mapping.FindValueByName(rawName);
				if (value != null)
				{
					unknown = null;
					return true;
				}
				unknown = rawName;
			}
            return false;
        }

        /// <summary>
        /// Reads an enum field value from the stream. If the enum is valid for type T,
        /// then the ref value is set and it returns true.  Otherwise the unkown output
        /// value is set and this method returns false.
        /// </summary>   
        public bool ReadEnum(ref int value, ref string name)
        {
			tokenizer.Consume(":");
			if (tokenizer.LookingAtInteger())
			{
				value = tokenizer.ConsumeInt32();
				name = null;
			}
			else
			{
				value = 0;
				name = tokenizer.ConsumeIdentifier();
			}
            return true;
        }

        /// <summary>
        /// Reads an sfixed32 field value from the stream.
        /// </summary>   
        public bool ReadSFixed32(ref int value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeInt32();
            return true;
        }

        /// <summary>
        /// Reads an sfixed64 field value from the stream.
        /// </summary>   
        public bool ReadSFixed64(ref long value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeInt64();
            return true;
        }

        /// <summary>
        /// Reads an sint32 field value from the stream.
        /// </summary>   
        public bool ReadSInt32(ref int value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeInt32();
            return true;
        }

        /// <summary>
        /// Reads an sint64 field value from the stream.
        /// </summary>   
        public bool ReadSInt64(ref long value)
        {
			tokenizer.Consume(":");
			value = tokenizer.ConsumeInt64();
            return true;
        }

        [CLSCompliant(false)]
        public void ReadPrimitiveArray(FieldType fieldType, uint fieldTag, string fieldName, ICollection<object> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
			{
				switch (fieldType)
				{
					case FieldType.Double: list.Add(tokenizer.ConsumeDouble()); break;
					case FieldType.Float: list.Add(tokenizer.ConsumeFloat()); break;
					case FieldType.Bool: list.Add(tokenizer.ConsumeBoolean()); break;
					case FieldType.Int32:
					case FieldType.SInt32:
					case FieldType.SFixed32: list.Add(tokenizer.ConsumeInt32()); break;
					case FieldType.Fixed32:
					case FieldType.UInt32: list.Add(tokenizer.ConsumeUInt32()); break;
					case FieldType.Int64:
					case FieldType.SInt64:
					case FieldType.SFixed64: list.Add(tokenizer.ConsumeInt64()); break;
					case FieldType.UInt64:
					case FieldType.Fixed64: list.Add(tokenizer.ConsumeUInt64()); break;
					case FieldType.Bytes: list.Add(tokenizer.ConsumeString()); break;
					case FieldType.String: list.Add(tokenizer.ConsumeByteString()); break;
					case FieldType.Group:
						throw new ArgumentException("ReadPrimitiveField() cannot handle nested groups.");
					case FieldType.Message:
						throw new ArgumentException("ReadPrimitiveField() cannot handle embedded messages.");
					// We don't handle enums because we don't know what to do if the
					// value is not recognized.
					case FieldType.Enum:
						throw new ArgumentException("ReadPrimitiveField() cannot handle enums.");
					default:
						throw new ArgumentOutOfRangeException("Invalid field type " + fieldType);
				}
			}
        }

        [CLSCompliant(false)]
        public void ReadStringArray(uint fieldTag, string fieldName, ICollection<string> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeString());
        }

        [CLSCompliant(false)]
        public void ReadBytesArray(uint fieldTag, string fieldName, ICollection<ByteString> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeByteString());
        }

        [CLSCompliant(false)]
        public void ReadBoolArray(uint fieldTag, string fieldName, ICollection<bool> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeBoolean());
        }

        [CLSCompliant(false)]
        public void ReadInt32Array(uint fieldTag, string fieldName, ICollection<int> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeInt32());
        }

        [CLSCompliant(false)]
        public void ReadSInt32Array(uint fieldTag, string fieldName, ICollection<int> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeInt32());
        }

        [CLSCompliant(false)]
        public void ReadUInt32Array(uint fieldTag, string fieldName, ICollection<uint> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeUInt32());
        }

        [CLSCompliant(false)]
        public void ReadFixed32Array(uint fieldTag, string fieldName, ICollection<uint> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeUInt32());
        }

        [CLSCompliant(false)]
        public void ReadSFixed32Array(uint fieldTag, string fieldName, ICollection<int> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeInt32());
        }

        [CLSCompliant(false)]
        public void ReadInt64Array(uint fieldTag, string fieldName, ICollection<long> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeInt64());
        }

        [CLSCompliant(false)]
        public void ReadSInt64Array(uint fieldTag, string fieldName, ICollection<long> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeInt64());
        }

        [CLSCompliant(false)]
        public void ReadUInt64Array(uint fieldTag, string fieldName, ICollection<ulong> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeUInt64());
        }

        [CLSCompliant(false)]
        public void ReadFixed64Array(uint fieldTag, string fieldName, ICollection<ulong> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeUInt64());
        }

        [CLSCompliant(false)]
        public void ReadSFixed64Array(uint fieldTag, string fieldName, ICollection<long> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeInt64());
        }

        [CLSCompliant(false)]
        public void ReadDoubleArray(uint fieldTag, string fieldName, ICollection<double> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeDouble());
        }

        [CLSCompliant(false)]
        public void ReadFloatArray(uint fieldTag, string fieldName, ICollection<float> list)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
				list.Add(tokenizer.ConsumeFloat());
        }

		public void ReadEnumArray(uint fieldTag, string fieldName, ICollection<IEnumLite> list,
								  out ICollection<object> unknown, IEnumLiteMap mapping)
		{
			unknown = null;
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
			{
				IEnumLite value;
				if (tokenizer.LookingAtInteger())
				{
					int rawValue = tokenizer.ConsumeInt32();
					value = mapping.FindValueByNumber(rawValue);
					if (value == null)
					{
						if (unknown == null)
							unknown = new List<object>();
						unknown.Add(rawValue);
					}
				}
				else
				{
					string rawName = tokenizer.ConsumeIdentifier();
					value = mapping.FindValueByName(rawName);
					if (value == null)
					{
						if (unknown == null)
							unknown = new List<object>();
						unknown.Add(rawName);
					}
				}
			}
		}

        public void ReadEnumArray(uint fieldTag, string fieldName, ref ICollection<object> values)
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
			{
				if (values == null)
					values = new List<object>();

				if (tokenizer.LookingAtInteger())
				{
					values.Add(tokenizer.ConsumeInt32());
				}
				else
				{
					values.Add(tokenizer.ConsumeIdentifier());
				}
			}
        }

        [CLSCompliant(false)]
        public void ReadMessageArray<T>(uint fieldTag, string fieldName, ICollection<T> list, T messageType,
                                        ExtensionRegistry registry) where T : IMessageLite
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
			{
                IBuilderLite builder = messageType.WeakCreateBuilderForType();
				if (++recursionDepth >= recursionLimit)
					throw InvalidProtocolBufferException.RecursionLimitExceeded();
				tokenizer.Consume("{");
				builder.WeakMergeFrom(this, registry);
				CheckLastTagWas("}");
				--recursionDepth;
                list.Add((T) builder.WeakBuildPartial());
			}
        }

        [CLSCompliant(false)]
        public void ReadGroupArray<T>(uint fieldTag, string fieldName, ICollection<T> list, T messageType,
                                      ExtensionRegistry registry) where T : IMessageLite
        {
			tokenizer.Consume(":");
			tokenizer.Consume("[");
			while (!tokenizer.TryConsume("]"))
			{
                IBuilderLite builder = messageType.WeakCreateBuilderForType();
				if (++recursionDepth >= recursionLimit)
					throw InvalidProtocolBufferException.RecursionLimitExceeded();
				tokenizer.Consume("{");
				builder.WeakMergeFrom(this, registry);
				CheckLastTagWas("}");
				--recursionDepth;
                list.Add((T) builder.WeakBuildPartial());
			}
        }

        /// <summary>
        /// Reads a field of any primitive type. Enums, groups and embedded
        /// messages are not handled by this method.
        /// </summary>
        public bool ReadPrimitiveField(FieldType fieldType, ref object value)
        {
            switch (fieldType)
            {
                case FieldType.Double:
                    {
                        double tmp = 0;
                        if (ReadDouble(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Float:
                    {
                        float tmp = 0;
                        if (ReadFloat(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Int64:
                    {
                        long tmp = 0;
                        if (ReadInt64(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.UInt64:
                    {
                        ulong tmp = 0;
                        if (ReadUInt64(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Int32:
                    {
                        int tmp = 0;
                        if (ReadInt32(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Fixed64:
                    {
                        ulong tmp = 0;
                        if (ReadFixed64(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Fixed32:
                    {
                        uint tmp = 0;
                        if (ReadFixed32(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Bool:
                    {
                        bool tmp = false;
                        if (ReadBool(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.String:
                    {
                        string tmp = null;
                        if (ReadString(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Bytes:
                    {
                        ByteString tmp = null;
                        if (ReadBytes(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.UInt32:
                    {
                        uint tmp = 0;
                        if (ReadUInt32(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.SFixed32:
                    {
                        int tmp = 0;
                        if (ReadSFixed32(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.SFixed64:
                    {
                        long tmp = 0;
                        if (ReadSFixed64(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.SInt32:
                    {
                        int tmp = 0;
                        if (ReadSInt32(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.SInt64:
                    {
                        long tmp = 0;
                        if (ReadSInt64(ref tmp))
                        {
                            value = tmp;
                            return true;
                        }
                        return false;
                    }
                case FieldType.Group:
                    throw new ArgumentException("ReadPrimitiveField() cannot handle nested groups.");
                case FieldType.Message:
                    throw new ArgumentException("ReadPrimitiveField() cannot handle embedded messages.");
                    // We don't handle enums because we don't know what to do if the
                    // value is not recognized.
                case FieldType.Enum:
                    throw new ArgumentException("ReadPrimitiveField() cannot handle enums.");
                default:
                    throw new ArgumentOutOfRangeException("Invalid field type " + fieldType);
            }
        }

        #endregion

        #region Underlying reading primitives

        /// <summary>
        /// Set the maximum message recursion depth.
        /// </summary>
        /// <remarks>
        /// In order to prevent malicious
        /// messages from causing stack overflows, CodedInputStream limits
        /// how deeply messages may be nested.  The default limit is 64.
        /// </remarks>
        public int SetRecursionLimit(int limit)
        {
            if (limit < 0)
            {
                throw new ArgumentOutOfRangeException("Recursion limit cannot be negative: " + limit);
            }
            int oldLimit = recursionLimit;
            recursionLimit = limit;
            return oldLimit;
        }
		#endregion

		#region Internal reading and buffer management

        /// <summary>
        /// Returns true if the stream has reached the end of the input. This is the
        /// case if either the end of the underlying input source has been reached or
        /// the stream has reached a limit created using PushLimit.
        /// </summary>
        public bool IsAtEnd
        {
			get { return tokenizer.AtEnd; }
        }

        /// <summary>
        /// Reads and discards a single field, given its tag value.
        /// </summary>
        /// <returns>false if the tag is an end-group tag, in which case
        /// nothing is skipped. Otherwise, returns true.</returns>
        [CLSCompliant(false)]
        public bool SkipField()
        {
			if (tokenizer.TryConsume("}"))
				return false;

			tokenizer.TryConsume(":");
			if (tokenizer.TryConsume("{"))
			{
				int balance = 1;
				while (balance != 0)
				{
					if (tokenizer.TryConsume("{"))
						balance++;
					if (tokenizer.TryConsume("}"))
						balance--;
					tokenizer.NextToken();
				}
			}
			if (tokenizer.TryConsume("["))
			{
				int balance = 1;
				while (balance != 0)
				{
					if (tokenizer.TryConsume("["))
						balance++;
					if (tokenizer.TryConsume("]"))
						balance--;
					tokenizer.NextToken();
				}
			}
			tokenizer.NextToken();
			return true;
        }

        /// <summary>
        /// Reads and discards an entire message.  This will read either until EOF
        /// or until an endgroup tag, whichever comes first.
        /// </summary>
        public void SkipMessage()
        {
            uint tag;
            string name;
            while (ReadTag(out tag, out name))
            {
                if (!SkipField())
                {
                    return;
                }
            }
        }
        #endregion
    }
}
#pragma warning restore
