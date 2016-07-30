package com.example.android.popmovies.utilities;

import java.util.Collection;

/**
 * Created by sheshloksamal on 26/03/16.
 *
 */
public class Lists {

    public static <E> boolean isEmpty(Collection<E> list){
        return (list == null || list.size() == 0);
    }
}
