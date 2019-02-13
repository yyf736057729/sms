package com.sanerzone.smscenter.gateway.sgip.comm;

import java.io.IOException;

/**
 * 协议编码器的父类。
 * @author 李大伟
 * @version 1.0
 */

public abstract class PWriter {

  /**
   * 向编码器写入一个消息。
   * @param message 待写入的消息。
   */
  public abstract void write(PMessage message) throws Exception;
}