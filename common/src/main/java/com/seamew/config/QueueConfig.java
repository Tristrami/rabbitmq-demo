package com.seamew.config;

public class QueueConfig
{
    public static final String SIMPLE_MODE_QUEUE_NAME = "simple";
    public static final String WORK_QUEUE_MODE_QUEUE_NAME = "work-queue";

    public static final String[] ROUTING_MODE_QUEUE_NAMES = { "error", "warn", "info" };

    public static final String[] ROUTING_MODE_KEYS = { "error", "warn", "info" };
}
