package uk.co.jatra.rxoperators;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by tim on 01/05/2016.
 */
public class LiftOrMap {


    private static final Func1<Integer, Boolean> everyOtherPredicate = new Func1<Integer, Boolean>() {
        int count;
        @Override
        public Boolean call(Integer integer) {
            return ++count%2 == 1;
        }
    };
    private static final Observable.Operator<Object, Integer> everyOtherOperator = new Observable.Operator<Object, Integer>() {
        int count;

        @Override
        public Subscriber<? super Integer> call(Subscriber<? super Object> subscriber) {
            return new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable throwable) {
                    subscriber.onError(throwable);
                }

                @Override
                public void onNext(Integer integer) {
                    if (++count % 2 == 1) {
                        subscriber.onNext(integer);
                    } else {
                        request(1);
                    }
                }
            };
        }
    };

    public static void main(String[] args) {
        Observable.range(1, 20)
                .subscribe(System.out::println);

        Observable.range(1, 20)
                .filter(everyOtherPredicate)
                .subscribe(System.out::println);

        Observable.range(1, 20)
                .lift(everyOtherOperator)
                .subscribe(System.out::println);
    }
}