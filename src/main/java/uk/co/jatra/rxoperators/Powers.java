package uk.co.jatra.rxoperators;

import rx.Observable;

/**
 * Created by tim on 03/05/2016.
 */
public class Powers {
    public static void main(String[] args) {
        Observable.range(1, 10)
                .flatMap(n -> power(n))
//                .flatMap(n -> power(n))
//                .flatMap(n -> power(n))
//                .flatMap(n -> power(n))
                .flatMap(n -> power(n))
                .subscribe(System.out::println);
    }

    public static Observable<Integer> power(int in) {
        return Observable.just(in * in);
    }
}
