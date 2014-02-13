package net.java.messageapi.adapter;

/** Return the destination name for a Pojo (maybe from an annotation) */
interface DestinationNameFunction {
    String apply(Object from);
}
