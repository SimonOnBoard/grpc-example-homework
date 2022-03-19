package com.mera.education.grpc.greeting.server;


import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetServiceImpl extends com.mera.education.grpc.proto.greet.GreetServiceGrpc.GreetServiceImplBase {

    private static Logger logger = LoggerFactory.getLogger(GreetServiceImpl.class);

    @Override
    public void greet(com.mera.education.grpc.proto.greet.GreetRequest request, StreamObserver<com.mera.education.grpc.proto.greet.GreetResponse> responseObserver) {
        logger.debug("*** Unary implementation on server side ***");
        com.mera.education.grpc.proto.greet.Greeting greeting = request.getGreeting();
        logger.debug("Request has been received on server side: firstName - {}, lastName - {}", greeting.getFirstName(), greeting.getLastName());

        String firstName = greeting.getFirstName();
        String result = "Hello " + firstName;

        com.mera.education.grpc.proto.greet.GreetResponse response = com.mera.education.grpc.proto.greet.GreetResponse.newBuilder()
                .setResult(result)
                .build();

        //send the response
        responseObserver.onNext(response);

        //complete RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetmanyTimes(com.mera.education.grpc.proto.greet.GreetManyTimesRequest request, StreamObserver<com.mera.education.grpc.proto.greet.GreetManyTimesResponse> responseObserver) {
        logger.debug("*** Server streaming implementation on server side ***");
        com.mera.education.grpc.proto.greet.Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number:" + (i + 1);
                com.mera.education.grpc.proto.greet.GreetManyTimesResponse response = com.mera.education.grpc.proto.greet.GreetManyTimesResponse.newBuilder()
                        .setResult(result)
                        .build();
                logger.debug("send response {} of 10", i + 1);
                responseObserver.onNext(response);
                Thread.sleep(1000L);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.debug("all messages have been sent");
            responseObserver.onCompleted();

        }

    }

    @Override
    public StreamObserver<com.mera.education.grpc.proto.greet.LongGreetRequest> longGreet(StreamObserver<com.mera.education.grpc.proto.greet.LongGreetResponse> responseObserver) {
        logger.debug("*** Client streaming implementation on server side ***");
        StreamObserver<com.mera.education.grpc.proto.greet.LongGreetRequest> streamObserverofRequest = new StreamObserver<com.mera.education.grpc.proto.greet.LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(com.mera.education.grpc.proto.greet.LongGreetRequest longGreetRequest) {
                logger.debug("make some calculation for each request");
                //client sends a message
                result += ". Hello " + longGreetRequest.getGreeting().getFirstName() + "!";
            }

            @Override
            public void onError(Throwable throwable) {
                //client sends an error
            }

            @Override
            public void onCompleted() {
//                client is done, this is when we want to return a response (responseObserver)
                responseObserver.onNext(com.mera.education.grpc.proto.greet.LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build());
                logger.debug("Send result: - {}",result);
                responseObserver.onCompleted();
            }
        };

        return streamObserverofRequest;
    }


    @Override
    public StreamObserver<com.mera.education.grpc.proto.greet.GreetEveryoneRequest> greetEveryone(StreamObserver<com.mera.education.grpc.proto.greet.GreetEveryoneResponse> responseObserver) {
        logger.debug("*** Bi directional streaming implementation on server side ***");

        StreamObserver<com.mera.education.grpc.proto.greet.GreetEveryoneRequest> requestObserver = new StreamObserver<com.mera.education.grpc.proto.greet.GreetEveryoneRequest>() {
            @Override
            public void onNext(com.mera.education.grpc.proto.greet.GreetEveryoneRequest value) {
                //client sends a message
                String result = "Hello " + value.getGreeting().getFirstName();
                com.mera.education.grpc.proto.greet.GreetEveryoneResponse greetEveryoneResponse = com.mera.education.grpc.proto.greet.GreetEveryoneResponse.newBuilder()
                        .setResult(result)
                        .build();

                //send message for each request
                logger.debug("Send result for each request: - {}",result);
                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                //nothing
            }

            @Override
            public void onCompleted() {
                //client is done, so complete server-side also
                logger.debug("close bi directional streaming");
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }
}
