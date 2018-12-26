namespace java com.seasy.microservice.api

include "Common.thrift"
		
service Hello{
	string helloString(1:string param)
	i32 helloInt(1:i32 param)
	bool helloBoolean(1:bool param)
	void helloVoid()
	Common.Response sendMessage(1:Common.Message message)
}
