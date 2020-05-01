package com.algoadvcd.gtfs.core.util;

public class TypeUtil {
	@SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object o)
    {
        return (T) o;
    }
}
