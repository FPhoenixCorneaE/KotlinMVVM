package com.fphoenixcorneae.adapter.internal

/**
 * 类型未注册异常
 */
class UnregisteredException(cls: Class<*>) : IllegalArgumentException("Have you registered " + cls.simpleName + ".class?")
