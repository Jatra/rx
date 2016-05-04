package uk.co.jatra.rx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static rx.Observable.just;

/**
 * Created by tim on 05/04/2016.
 */

public class Rx {

    //Test Observable, emits 2 strings.
    private static Observable<String> createStringEmittingObservable() {
        return just("Hello world!", "Goodbye cruel world");
    }

    public static void main(String[] args) {

        log("===Log each item emitted, one per line========");

        //log each item
        createStringEmittingObservable()
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }
                });

        log("===========");

        log("===Log each item emitted, one per line========");

        //log each item
        createStringEmittingObservable()
                .subscribe(System.out::println);

        log("===========");

        log("===map: replace each item by another========");
        //map:  each item is replaced by another item

        //log the words of each item
        //maps the types of each item. Same number of emissions
        createStringEmittingObservable()
                .map(new Func1<String, String[]>() {
                    @Override
                    public String[] call(String s) {
                        return s.split(" ");
                    }
                }) //emits Lists
                .subscribe(System.out::println);

        log("===========");


        log("===map: replace each item by another========");
        //map:  each item is replaced by another item
        //Same as above, but using lamdba

        //log the words of each item
        //maps the types of each item. Same number of emissions
        createStringEmittingObservable()
                .map(s -> s.split(" ")) //emits Lists
                .subscribe(System.out::println);

        log("===========");

        log("===map: replace each item by another========");

        //log the first word of each item
        //Same number of emissions
        createStringEmittingObservable()
                .map(s -> s.split(" ")[0])  //emits Strings
                .subscribe(System.out::println);

        log("===========");

        log("===flatMap: replace each item by a different number of items========");
        //flatMap: each item is replaced by > 1 item.

        //log each word of each item
        //changes how many emissions.
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" "))) //emits strings, possibly multiple per input string
                .subscribe(System.out::println);

        log("===========");

        log("===flatMap: replace each item by a different number of UNRELATED items========");
        //flatMap: each item is replaced by > 1 item.

        //log each word of each item
        //changes how many emissions.
        createStringEmittingObservable()
                .flatMap(s -> just(1, 2, 3)) //emits strings, possibly multiple per input string
                .subscribe(System.out::println);

        log("===========");

        log("===flatMap: replace each item by a possibly zero, varying number of items========");
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" "))) //emits strings, possibly multiple per input string
                .flatMap(s -> {
                    if ("Hello".equals(s)) {
                        return Observable.just("hello", "hi", "howdy");
                    } else if ("cruel".equals(s)) {
                        return Observable.empty(); //is it valid to simply return null?
                    } else {
                        return Observable.just(s);
                    }
                })
                .subscribe(System.out::println);

        log("===========");

        log("===flatMap: replace each item by a possibly zero, different number of UNRELATED items========");
        //flatMap: each item is replaced by > 1 item.

        //log each word of each item
        //changes how many emissions.
        createStringEmittingObservable()
                .flatMap(new Func1<String, Observable<? extends String>>() {
                    @Override
                    public Observable<? extends String> call(String s) {
                        if (s.startsWith("Hello")) {
                            return null;        //is this a valid way of not emitting anything??
                            //maybe better to return Observable.empty() ?
                            //Since it's a flatmap, then the Observable this supplies doesnt emit.
                        } else {
                            return Observable.just("red", "orange", "apple", "google");
                        }
                    }
                }) //emits strings, possibly multiple per input string
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (s.equals("orange")) {
                            return null;    //actually emits a null
                        } else {
                            return s;
                        }
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s1) {
                        Rx.log(s1);
                    }
                });   //Note this will be called BEFORE the flatmap and map functions. Nothing is emitted until a subscriber subscribes (unless its warm?)

        log("===========");


        log("===flatMap: + map: change number and type of items========");
        //log the length of each word of each item
        //changes how many emissions. Different type.
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" ")))
                .map(s -> s.length())
                .subscribe(System.out::println);

        log("===========");


        //log the length of each word of each item
        //changes how many emissions. Same type, but transformed
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" ")))
                .map(s -> "Length is " + s.length())
                .subscribe(System.out::println);

        log("===========");

        //log the length of each word of each item
        //changes how many emissions. Same type, but transformed
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" ")))
                .map(s -> s.length())
                .map(s -> "Length is " + s)
                .subscribe(System.out::println);

        log("===========");

        //log the length of each word of each item
        //changes how many emissions. Same type, but transformed
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" ")))
                .map(s -> s + " is " + s.length() + " characters long")
                .subscribe(System.out::println);

        log("===========");

        log("===flatMap: + map: change number and type of items========");
        //log the length of each word of each item
        //changes how many emissions.
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" ")))
                .map(getStringLength())
                //same as:
                //.map(s1 -> s1 + " is " + s1.length() + " characters long")
                .subscribe(System.out::println);

        log("===========");

        log("===flatMap: + map: change number and type of items using lift ========");
        //log the length of each word of each item
        //changes how many emissions. Same type, but transformed by *** own operator ***
        createStringEmittingObservable()
                .flatMap(s -> Observable.from(s.split(" ")))
                .lift(new StringLengthOperator())
                .subscribe(System.out::println);

        log("===========");


        createStringEmittingObservable()
                .flatMap(new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String s) {
                        return just(s);
                    }
                })
                .map(s -> just(s))
                .flatMap(s -> s)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        log(o.toString());
                    }
                });

        log("===========");

        log("===Schedulers using map=========");

        createStringEmittingObservable()
            .observeOn(Schedulers.computation())
//                .subscribeOn(Schedulers.computation())
                .map(s -> s+" on Thread "+Thread.currentThread().getName())
                .subscribe(System.out::println);

        log("===========");


        log("===Schedulers using lift=========");

        createStringEmittingObservable()
            .observeOn(Schedulers.computation())
//                .subscribeOn(Schedulers.computation())
                .lift(new StringLengthOperator(true))
                .subscribe(System.out::println);

        log("===========");


        log("===Handle errors in the main stream========");
        Observable.just("Hello!")
                .map(input -> {
                    throw new RuntimeException("Exception from map");
                })
                .onErrorReturn(e -> "Error handling: " + e.getMessage())
                .map(s -> "Next item: " + s)
                .subscribe(item -> {
                            log("lognext: " + item);
                        },
                        error -> {
                            log("logerror: " + error.getMessage() + " ***** SHOULDNT BE SEEN ");
                        }
                );

        log("===========");

        log("===artificial errors========");

        Observable<String> objectObservable = Observable.create(subscriber -> {
            subscriber.onNext("foo");
            subscriber.onNext("bar");
            subscriber.onError(new RuntimeException("ding!"));
        });
        objectObservable
                .timeout(5, TimeUnit.SECONDS, timeoutError())
                .subscribe(System.out::println, e -> {
                    log("Error: " + e.getMessage());
                });

        log("===========");


        log("===operators========");

        Observable.Operator<Boolean, Integer> isOdd = new Observable.Operator<Boolean, Integer>() {
            @Override
            public Subscriber<? super Integer> call(Subscriber<? super Boolean> child) {

                return new Subscriber<Integer>(child) {                    // (1)
                    @Override
                    public void onNext(Integer value) {
                        child.onNext((value & 1) != 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onCompleted() {
                        child.onCompleted();
                    }
                };
            }
        };

        //lambda is where a Functor is used. Ie creation of an instance of a type that has a single argument.
        Observable.Operator<Boolean, Integer> isOddLambda = child -> {

            return new Subscriber<Integer>(child) {                    // (1)
                @Override
                public void onNext(Integer value) {
                    child.onNext((value & 1) != 0);
                }
                @Override
                public void onError(Throwable e) {
                    child.onError(e);
                }
                @Override
                public void onCompleted() {
                    child.onCompleted();
                }
            };
        };


        int count = 2_000_000_000;
        Observable.range(1, count)
                .lift(isOdd)
                .take(2)
                .subscribe(System.out::println);


        log("===========");

        log("===time out errors========");

        Observable.create(subscriber -> {
            subscriber.onNext("foo");
            try {
                Thread.currentThread().sleep(2000);
            } catch (Exception e) {
                subscriber.onError(e);
            }
            subscriber.onNext("bar");
            subscriber.onError(new RuntimeException("ding!"));
        })
                .timeout(1, TimeUnit.SECONDS, timeoutError())
                .subscribe(System.out::println,
                        e -> {
                            log("Error: " + e.getMessage());
                        });
        log("===========");

        log("===double block in chain========");

        Observable.create(subscriber -> {
            String data = "string stub";
            log("Before 1st block...");
            try {
                data += "[from first: " + System.in.read() + "]";
            } catch (Exception e) {
                subscriber.onError(e);
            }
            subscriber.onNext(data);
        })
                .map(s -> {
                    log("Before 2nd block");
                    try {
                        return s + "<from second: " + System.in.read() + ">";
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot read");
                    }
                })
                .timeout(20, TimeUnit.SECONDS)   //just so I dont leave this running
                .subscribe(System.out::println, System.err::println);
        log("===========");

        log("===double block in chain 2========");

        Observable.create(subscriber -> {
            String data = "";
            try {
                data += "[from first: " + System.in.read() + "]";
            } catch (Exception e) {
                subscriber.onError(e);
            }
            subscriber.onNext(data);
        })
                .flatMap( s -> Observable.create(subscriber -> {
                    subscriber.onNext(s);
                    try {
                        subscriber.onNext("<from second: " + System.in.read() + ">");
                    } catch (IOException e) {
                        subscriber.onNext("<second exception: "+e.getMessage()+">");
                    }
                    finally {
                        subscriber.onCompleted();
                    }
                }))
                .timeout(20, TimeUnit.SECONDS)   //just so I dont leave this running
                .subscribe(System.out::println, System.err::println);
        log("===========");

    }

    private static Observable<String> timeoutError() {
        return Observable.error(new RuntimeException("Time out"));
    }

    private static Func1<String, String> getStringLength() {
        return s -> s + " is " + s.length() + " characters long";
    }

    static void log(Object o) {
        System.out.println(o.toString());
    }
    static void log(String s) {
        System.out.println(s);
    }

    static void log(int i) {
        System.out.println(Integer.toString(i));
    }

    private static void log(String[] strings) {
        System.out.println(Arrays.toString(strings));
    }

    private static class StringLengthOperator implements Observable.Operator<String, String> {

        private boolean mLogThreadName;

        public StringLengthOperator() {
            this(false);
        }

        public StringLengthOperator(boolean logThreadName) {
            mLogThreadName = logThreadName;
        }

        @Override
        public Subscriber<? super String> call(Subscriber<? super String> subscriber) {
            return new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(throwable);
                    }
                }

                @Override
                public void onNext(String s) {
                    if (!subscriber.isUnsubscribed()) {
                        if (mLogThreadName) {
                            subscriber.onNext(s + " is " + s.length() + " characters long. "+threadName());
                        }
                        else {
                            subscriber.onNext(s + " is " + s.length() + " characters long. ");
                        }
                    }
                }
                private String threadName() {
                    return "on thread "+Thread.currentThread().getName();
                }
            };
        }
    }

    private Observable<Integer> lengthOfEachWordInSentence(String words) {
        return Observable.from(words.split(" "))
                .map(s -> s.length());
    }

    private static class MyOp implements Observable.Operator<String, Integer> {
        @Override
        public Subscriber<? super Integer> call(Subscriber<? super String> subscriber) {
            return new IntegerToStringSubscriber(subscriber);
        }
    }




    private abstract static class CheckingSubscriber<T, U> extends Subscriber<T> {
        private Subscriber<U> subscriber;
        public CheckingSubscriber(Subscriber<U> subscriber) {
            this.subscriber = subscriber;
        }
        @Override
        public void onCompleted() {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }
        @Override
        public void onError(Throwable throwable) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onError(throwable);
            }
        }
        @Override
        public void onNext(T item) {
            if (!subscriber.isUnsubscribed()) {
                U converted = convert(item);
                subscriber.onNext(converted);
            }
        }
        protected abstract U convert(T item);
    }

    private static class IntegerToStringSubscriber extends CheckingSubscriber<Integer, String> {
        public IntegerToStringSubscriber(Subscriber subscriber) {
            super(subscriber);
        }
        @Override
        protected String convert(Integer item) {
            return item.toString();
        }
    }

//    private static class IntegerSubscriber extends Subscriber<Integer> {
//        private final Subscriber<? super String> mSubscriber;
//
//        public IntegerSubscriber(Subscriber<? super String> subscriber) {
//            mSubscriber = subscriber;
//        }
//
//        @Override
//        public void onCompleted() {
//            if (!mSubscriber.isUnsubscribed()) {
//                mSubscriber.onCompleted();
//            }
//        }
//
//        @Override
//        public void onError(Throwable throwable) {
//            if (!mSubscriber.isUnsubscribed()) {
//                mSubscriber.onError(throwable);
//            }
//        }
//
//        @Override
//        public void onNext(Integer integer) {
//            if (!mSubscriber.isUnsubscribed()) {
//                mSubscriber.onNext(integer.toString());
//            }
//        }
//    }


}
