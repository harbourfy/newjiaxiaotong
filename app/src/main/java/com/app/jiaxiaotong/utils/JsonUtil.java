package com.app.jiaxiaotong.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

/**
 * 将对象或对象数组转换为json字符串
 * 
 * @author yikelizi
 * 
 */
public class JsonUtil {

	/**
	 * 传入任意一个 javabean 对象生成一个指定规格的字符串
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static String beanToJson(Object bean) throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {

					Object obj = props[i].getReadMethod().invoke(bean);
					if (obj != null && obj != "null") {
//						if (obj instanceof List) {
//							List<?> newObj = (List<?>) obj;
//							if (newObj != null && newObj.size() > 0) {
//								List<String> newList = new ArrayList<String>();
//								for (int j = 0; j < newObj.size(); j++) {
//									Object objs = newObj.get(j);
//									if (objs != null) {
//										if (objs instanceof BaseModel) {
//											newList.add(beanToJson(objs));
//										}
//									}
//								}
//								if (newList != null && newList.size() > 0) {
//									obj = newList;
//								}
//
//							}
//						} else if (obj instanceof BaseModel) {
//							obj = beanToJson(obj);
//						}
						map.put(props[i].getName(), obj);
					}

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return convertToJsonString(map);
	}

	/**
	 * 转换Map为JSON字符串
	 * 
	 * @param map
	 *            待转换的Map集合
	 * @return JSON字符串
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static String convertToJsonString(Map<? extends Object, ? extends Object> map) throws JsonGenerationException, JsonMappingException, IOException {
		String jsonString = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, map);
		jsonString = writer.toString();
//		try {
//			jsonString = new ObjectMapper().writeValueAsString(map);
//		}  catch (IOException e) {
//			e.printStackTrace();
//		}
		return jsonString;
	}
	
	 /**
     * <b>function:</b>将java对象转换成JSON字符串
     *
     * @param obj
     * @return
     */
    public static <T> String toJsonString(T obj) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, obj);
            return writer.toString();
        } catch (Exception e) {
            throw new Exception();
        }
    }

    /**
     * <b>function:</b>将json字符串转换成JavaBean对象
     *
     * @param content json字符串
     * @param clazz   JavaBean class 对象
     * @return JavaBean对象
     */
    public static <T> T readJson2Entity(String content, Class<T> clazz) {
        if ("".equals(content)) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(content, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <b>function:</b>将服务器返回的数据转换成JavaBean对象
     *
     * @param responseBody 服务器返回的数据
     * @param clazz        JavaBean class 对象
     * @return JavaBean对象
     * @throws Exception 
     */
    public static <T> T readJson2Entity(byte[] responseBody, Class<T> clazz) throws Exception {
        if (null == responseBody) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(new String(responseBody), clazz);
        } catch (Exception e) {
        	throw new Exception();
//            throw new ProcessorException(ErrorCode.ERROR_CODE_JSON_PARSE_JSON_2_OBJECT, e);
        }
    }

    /**
     * <b>function:</b>将JSON字符串转换成List
     *
     * @param content
     * @param listClaszz
     * @param beansClaszz
     * @return
     * @throws Exception 
     */
    public static List<?> readJson2List(String content, Class<?> listClaszz, Class<?>[] beansClaszz) throws Exception {
        if (null == content) {
            return null;
        }

        try {
            ObjectMapper localObjectMapper = new ObjectMapper();
            localObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return (List<?>) localObjectMapper.readValue(content, getCollectionType(listClaszz, beansClaszz));
        } catch (Exception e) {
        	throw new Exception();
//            throw new ProcessorException(ErrorCode.ERROR_CODE_JSON_PARSE_JSON_2_OBJECT, e);
        }
    }

    private static JavaType getCollectionType(Class<?> paramClass, Class<?>[] paramArrayOfClass) {
        return new ObjectMapper().getTypeFactory().constructParametricType(paramClass, paramArrayOfClass);
    }
}
