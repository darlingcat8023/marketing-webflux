package com.estar.marketing.admin.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author xiaowenrou
 * @date 2023/4/23
 */
public abstract class EasyExcelUtils {

    /**
     * 响应式读取Excel的方法
     * @param inputStream
     * @param clazz
     * @param sheetNo
     * @return
     * @param <T>
     */
    public static <T> Flux<T> read(final InputStream inputStream, Class<T> clazz, int sheetNo) {
        return Flux.push(emitter -> EasyExcel.read(inputStream, clazz, new AnalysisEventListener<T>() {
            @Override
            public void invoke(T t, AnalysisContext analysisContext) { emitter.next(t);}
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) { emitter.complete();}
            @Override
            public void onException(Exception exception, AnalysisContext context) { emitter.error(exception);}
        }).sheet(sheetNo).doRead(), FluxSink.OverflowStrategy.ERROR);
    }

    /**
     * 响应式使用字节读取
     * @param bytes
     * @param clazz
     * @param sheetNo
     * @return
     * @param <T>
     */
    public static <T> Flux<T> read(final byte[] bytes, Class<T> clazz, int sheetNo) {
        return read(new ByteArrayInputStream(bytes), clazz, sheetNo);
    }


}
