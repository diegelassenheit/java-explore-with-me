package ru.practicum.ewm.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Да, рефлексии у нас в курсе не было. Но генерировать копипасту за генераторы кода тоже надоедает.
// Вот прям очень стойкое ощущение, что для спринга должен быть какой-нибудь DSL-плагин, в котором
// можно накидать основные сущности, может, через UML или типа того, и оно нагенерирует и Dto, модели,
// вспомогательные классы и прочие контроллеры-сервисы-мапперы-репозитории.
// Потому что вот я вроде пишу на джаве и спринге, но CRUD - везде CRUD,
// поэтому приходится писать Q-классы, как в джанге (там правда, в норме их писать самостоятельно не надо).
// Я вроде нагуглил какие-то генераторы, которые умеют переваривать OpenAPI-спеку, но побоялся, что они будут
// генерировать сильно не так, как надо практикуму, поэтому забил и не стал оттуда все использовать. Может, и зря.
// В гошном практикуме (на другой платформе) пред-выпускным заданием было заюзать Swagger для генерации апишки.
// Мне тогда не понравилось, потому что разбираться в написанном не тобой коде непривычно. Но после этого диплома
// я точно уверую в swagger.:)
// Может, ссылка на openapi спеку в задании это и был намек на сваггер? Вот это я лажанулся тогда)
// В чатике курса еще вот такое упоминают: https://mapstruct.org/documentation/stable/reference/html/#updating-bean-instances
// но выглядит не лучше, имхо.

public class EwmUtils {
    public static <T> void copyNotNullProperties(T source, T target) {
        BeanUtils.copyProperties(source, target, getNullValueProperties(source));
    }

    private static <T> Map<String, String> getGetterAndFieldMatch(T source) {
        return Arrays.stream(source.getClass().getDeclaredFields())
                .filter(field -> field.getModifiers() == Modifier.PRIVATE)
                .map(field -> {
                    String capitalizeName = field.getName()
                            .substring(0, 1)
                            .toUpperCase()
                            .concat(field.getName().substring(1));
                    String getter = "get".concat(capitalizeName);
                    String[] result = new String[2];
                    result[0] = getter;
                    result[1] = field.getName();
                    return result;

                }).collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private static <T> String[] getNullValueProperties(T source) {
        Method[] methods = source.getClass().getMethods();
        Map<String, String> privateFields = getGetterAndFieldMatch(source);

        List<String> nullPropertiesList = new ArrayList<>();
        for (Method method : methods) {
            if (method.getModifiers() == Modifier.PUBLIC
                    && method.getName().startsWith("get")
                    && method.getParameters().length == 0) {

                try {
                    if (method.invoke(source) == null
                            && privateFields.containsKey(method.getName())) {
                        nullPropertiesList.add(privateFields.get(method.getName()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return nullPropertiesList.toArray(new String[0]);
    }


}