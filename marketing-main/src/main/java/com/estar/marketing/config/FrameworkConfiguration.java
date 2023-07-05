package com.estar.marketing.config;

import com.estar.marketing.admin.AdminPropertiesConfiguration;
import com.estar.marketing.client.ClientPropertiesConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.estar.marketing.base.Constants.*;

/**
 * @author xiaowenrou
 * @date 2022/7/28
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {
        AdminPropertiesConfiguration.RootUser.class,
        ClientPropertiesConfiguration.ProviderHonghe.class
})
public class FrameworkConfiguration {

    @Bean
    public Validator validator() {
        var validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .addProperty( "hibernate.validator.fail_fast", "true" )
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        var implicitRecordAI = new JacksonAnnotationIntrospector() {
            @Override
            public PropertyName findNameForDeserialization(Annotated a) {
                var nameForDeserialization = super.findNameForDeserialization(a);
                if (PropertyName.USE_DEFAULT.equals(nameForDeserialization) && a instanceof AnnotatedParameter && ((AnnotatedParameter) a).getDeclaringClass().isRecord()) {
                    var str = findImplicitPropertyName((AnnotatedParameter) a);
                    if (str != null && !str.isEmpty()) {
                        return PropertyName.construct(str);
                    }
                }
                return nameForDeserialization;
            }

            @Override
            public String findImplicitPropertyName(AnnotatedMember m) {
                if (m.getDeclaringClass().isRecord()) {
                    if (m instanceof AnnotatedParameter parameter) {
                        return m.getDeclaringClass().getRecordComponents()[parameter.getIndex()].getName();
                    }
                    for (var recordComponent : m.getDeclaringClass().getRecordComponents()) {
                        if (recordComponent.getName().equals(m.getName())) {
                            return m.getName();
                        }
                    }
                }
                return super.findImplicitPropertyName(m);
            }
        };
        // 配置日期类型序列化和反序列化
        var timeModule = new JavaTimeModule();

        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));

        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));

        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));

        return builder.createXmlMapper(false)
                .annotationIntrospector(implicitRecordAI)
                .modules(timeModule)
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .build();
    }


}
