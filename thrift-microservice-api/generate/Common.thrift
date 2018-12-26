namespace java com.seasy.microservice.api

struct Message {
	1: i32 type;
	2: binary data;
}

struct Response {
	1: i32 code;
	2: string message;
}
