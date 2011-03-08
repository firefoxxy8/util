package com.twitter.util

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.apache.commons.codec.binary.Base64
import org.apache.thrift.TBase
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol, TProtocolFactory,
  TSimpleJSONProtocol}
import org.apache.thrift.transport.TIOStreamTransport

trait ThriftSerializer {
  def protocolFactory: TProtocolFactory

  def toBytes(obj: TBase[_, _]): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    obj.write(protocolFactory.getProtocol(new TIOStreamTransport(baos)))
    baos.toByteArray
  }

  def fromBytes(obj: TBase[_, _], bytes: Array[Byte]): Unit =
    obj.read(protocolFactory.getProtocol(new TIOStreamTransport(new ByteArrayInputStream(bytes))))
}

class JsonThriftSerializer extends ThriftSerializer {
  override def protocolFactory = new TSimpleJSONProtocol.Factory

  def toString(obj: TBase[_, _]): String = new String(toBytes(obj))

  /**
   * Thrift does not properly deserialize the JSON it serializes ;/
   */
  def fromString(obj: TBase[_, _], str: String): Unit =
    throw new UnsupportedOperationException("Thrift does not properly deserialize the JSON")

  /**
   * Thrift does not properly deserialize the JSON it serializes ;/
   */
  override def fromBytes(obj: TBase[_, _], bytes: Array[Byte]): Unit =
    throw new UnsupportedOperationException("Thrift does not properly deserialize the JSON")
}

class BinaryThriftSerializer extends ThriftSerializer {
  override def protocolFactory = new TBinaryProtocol.Factory
}

class CompactThriftSerializer extends ThriftSerializer {
  override def protocolFactory = new TCompactProtocol.Factory
}