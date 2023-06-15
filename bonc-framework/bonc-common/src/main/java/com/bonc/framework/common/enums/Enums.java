package com.bonc.framework.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 枚举集合
 *
 * @author zhangxiaonan
 */
public interface Enums {

    @Getter
    enum AsyncStatus {
        STATE_PENDING(0, "waiting"), STATE_PROCESSING(1, "handling"), STATE_SUCCESS(2, "success"), STATE_VALIDATE_FAIL
                (-1, "checkfailed"), STATE_PARSE_FAIL(-2, "parsingfailed"), STATE_EXEC_FAIL(-3, "failed");

        AsyncStatus(Integer value, String name) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private int value;

        private static final Map<Integer, String> map = new HashMap<Integer, String>();

        static {
            for(AsyncStatus asyncStatus : AsyncStatus.values()){
                map.put( asyncStatus.getValue(),asyncStatus.getName());

            }
        }
        public static String getNameByValue(Integer value){
            return map.get(value);
        }
    }

    @Getter
    enum UpdDatasetStatus {
        STATE_PENDING(0, "waiting"), STATE_HANDLING(1, "handling"), STATE_SUCCESS(2, "success"), STATE_NETWORKEXCEPTION
                (-1, "sftpconnectfaild"), STATE_FILEPATHEXCEPTION(-2, "filepathexception"), STATE_EXEC_FAIL(-3, "failed");

        UpdDatasetStatus(Integer value, String name) {
            this.name = name;
            this.value = value;
        }

        private String name;
        private int value;

        private static final Map<Integer, String> map = new HashMap<Integer, String>();

        static {
            for(UpdDatasetStatus updDatasetStatus : UpdDatasetStatus.values()){
                map.put( updDatasetStatus.getValue(),updDatasetStatus.getName());

            }
        }
        public static String getNameByValue(Integer value){
            return map.get(value);
        }
    }
}
