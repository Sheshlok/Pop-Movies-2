package com.example.android.popmovies.Networking;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by sheshloksamal on 12/03/16.
 * A sample showing a custom CallAdapter which adapts the built-in Call to a custom version
 * whose callback has more granular methods
 */
public class ErrorHandlingCallAdapter {

    /**
     * A callback which offers granular callbacks for various conditions.
     */

    interface MyCallback<T> {

        /**
         * Called for [200, 300) responses.
         */
        void success(Response<T> response);

        /**
         * Called for 401 responses.
         */
        void unauthenticated(Response<?> response);

        /**
         * Called for [400, 500) responses, except 401.
         */
        void clientError(Response<?> response);

        /**
         * Called for [500, 600) responses.
         */
        void serverError(Response<T> response);

        /**
         * Called for network errors while making the call.
         */
        void networkError(IOException e);

        /**
         * Called for unexpected errors while making the call.
         */
        void unExpectedError(Throwable t);

    }

    interface MyCall<T> {

        void cancel();

        void enqueue(MyCallback<T> myCallback);

        MyCall<T> clone();

        MyResponse<T> execute();

    }

    public static class MyResponse<T> {

        public static <T> MyResponse<T> exception(Exception exception) {
            if (exception == null) throw new NullPointerException("Exception == null");
            return new MyResponse<>(null, exception);
        }

        public static <T> MyResponse<T> response(Response<T> response) {
            if (response == null) throw new NullPointerException("Response == null");
            return new MyResponse<>(response, null);
        }

        private final Response<T> response;
        private final Exception exception;

        private MyResponse(Response<T> response, Exception exception) {
            this.response = response;
            this.exception = exception;
        }

        public Response<T> getResponse() {
            return this.response;
        }

        public Exception getException() {
            return this.exception;
        }

        public boolean isException() {
            return exception != null;
        }
    }


    public static class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

        @Override
        public CallAdapter<MyCall<?>> get(Type returnType, Annotation[] annotations,
                                          Retrofit retrofit) {
            if (getRawType(returnType) != MyCall.class) {
                return null;
            }

            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "MyCall must have a generic type (for e.g. MyCall<ResponseBody>");
            }

            final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
            final Executor callbackExecutor = retrofit.callbackExecutor();

            return new CallAdapter<MyCall<?>>() {
                @Override
                public Type responseType() {
                    return responseType;
                }

                @Override
                public <R> MyCall<?> adapt(Call<R> call) {
                    return new MyCallAdapter<>(call, callbackExecutor);
                }
            };
        }
    }

    /**
     * Adapts a Call to MyCall
     */
    static class MyCallAdapter<T> implements MyCall<T> {

        private Call<T> call;
        private Executor callbackExecutor;

        MyCallAdapter(Call<T> call, Executor callbackExecutor) {
            this.call = call;
            this.callbackExecutor = callbackExecutor;
        }

        @Override
        public void cancel() {
            call.cancel();
        }

        @Override
        public void enqueue(final MyCallback<T> callback) {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {

                    int code = response.code();
                    if (code >= 200 && code < 300) {
                        callback.success(response);
                    } else if (code == 401) {
                        callback.unauthenticated(response);
                    } else if (code >= 400 && code < 500) {
                        callback.clientError(response);
                    } else if (code >= 500 && code < 600) {
                        callback.serverError(response);
                    } else {
                        callback.unExpectedError(new RuntimeException("Unexpected Response" + response));
                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {

                    if (t instanceof IOException) {
                        callback.networkError((IOException) t);
                    } else {
                        callback.unExpectedError(t);
                    }

                }
            });
        }

        @Override
        public MyCall<T> clone() {
            return new MyCallAdapter<>(call.clone(), callbackExecutor);
        }

        @Override
        public MyResponse<T> execute() {
            try {
                return MyResponse.response(call.execute());
            } catch (IOException e) {
                return MyResponse.exception(e);
            }
        }
    }

}
