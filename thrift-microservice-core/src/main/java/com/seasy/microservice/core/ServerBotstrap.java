package com.seasy.microservice.core;

import java.util.concurrent.atomic.AtomicInteger;

public interface ServerBotstrap extends Bootstrap{
	int getPort();
	AtomicInteger getConnectCount();
}