namespace java com.seasy.microservice.api

include "Common.thrift"
	
service Device{
	void init()
	void unInit()
}

service FingerScannerDevice extends Device{
	string helloString(1:string param)
	i32 helloInt(1:i32 param)
	bool helloBoolean(1:bool param)
	void helloVoid()
}

service CardReaderDevice extends Device{
	Common.Response sendMessage(1:Common.Message message)
}
