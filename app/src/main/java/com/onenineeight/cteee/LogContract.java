package com.onenineeight.cteee;

import android.provider.BaseColumns;

public final class LogContract {
    private LogContract() {}


    public static class LogTable implements BaseColumns {
        public static final String TABLE_NAME = "log_table";
        public static final String COLUMN_LOG = "beacon";
        public static final String COLUMN_1 = "time";
        public static final String COLUMN_2 = "duration";
    }
}
