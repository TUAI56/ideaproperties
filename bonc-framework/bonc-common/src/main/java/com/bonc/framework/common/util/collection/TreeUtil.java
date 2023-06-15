package com.bonc.framework.common.util.collection;



import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class TreeUtil {

    /**
     * @param list          树结构的基础数据集
     * @param getIdFn       获取主键的函数
     * @param getParentIdFn 获取父节点的函数
     * @param getChildrenFn 获取子集的函数
     * @param <T>           实体类型
     * @param <R>           主键的数据类型
     * @return t
     */
    @SneakyThrows
    public static <T, R> List<T> treeOut(List<T> list, Function<T, R> getIdFn, Function<T, R> getParentIdFn, Function<T, R> getChildrenFn) {
        /*所有元素的Id*/
        List<Object> ids = list.stream().map(getIdFn).collect(Collectors.toList());
        /*查出所有顶级节点*/
        List<T> topLevel = list.stream().filter(x -> {
            R apply = getParentIdFn.apply(x);
            return !ids.contains(apply);
        }).collect(Collectors.toList());
        return recursion(topLevel, list, getIdFn, getParentIdFn, getChildrenFn);
    }


    /**
     *
     * @param superLevel
     * @param list
     * @param getIdFn
     * @param getParentIdFn
     * @param getChildrenFn
     * @return
     * @param <T>
     * @param <R>
     */
    @SneakyThrows
    private static <T, R> List<T> recursion(List<T> superLevel, List<T> list, Function<T, R> getIdFn, Function<T, R> getParentIdFn, Function<T, R> getChildrenFn) {
        //获取setChildren的Method
        Method writeReplaceMethod = getChildrenFn.getClass().getDeclaredMethod("writeReplace");
        boolean accessible = writeReplaceMethod.isAccessible();
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(getChildrenFn);
        writeReplaceMethod.setAccessible(accessible);
        String setMethodName = serializedLambda.getImplMethodName().replaceFirst("g", "s");
        Method setMethod = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredMethod(setMethodName, List.class);

        for (T t : superLevel) {
            List<T> children = list.stream().filter(x -> {
                R apply = getParentIdFn.apply(x);
                R apply1 = getIdFn.apply(t);
                return apply.equals(apply1);
            }).collect(Collectors.toList());
            if (children.size() <= 0) {
                continue;
            }

            List<T> recursion = recursion(children, list, getIdFn, getParentIdFn, getChildrenFn);
            setMethod.invoke(t, recursion);
        }
        return superLevel;
    }


    /**
     * 将List 集合转为树结构
     * @param list
     * @return
     * @param <T>
     */
    public static <T extends TreeNode> List<T> listToTree(List<T> list){
        List<String> ids = list.stream().map(e -> e.getDirId()).collect(Collectors.toList());
        List<T> parentLevel = list.stream().filter(e -> !ids.contains(e.getParentDirId())).collect(Collectors.toList());
        return setChildrenList(parentLevel,list);
    }

    /**
     * 进行循环处理,设置父类的子类集合
     * @param parentLevel
     * @param list
     * @return
     * @param <T>
     */
    private static <T extends TreeNode> List<T> setChildrenList(List<T> parentLevel, List<T> list) {
        for (T t:parentLevel) {
            List<T> children = list.stream().filter(e -> e.getParentDirId().equals(t.getDirId())).collect(Collectors.toList());
            if(children.size() == 0){
                t.setChildren(new ArrayList<T>());
                continue;
            }
            children=setChildrenList(children,list);
            t.setChildren(children);
        }
        return parentLevel;
    }

    /**
     * 将tree 转为 List 集合
     * @param list 树图结构
     * @return
     * @param <T>
     */
    public static <T extends TreeNode> List<T> treeToList(List<T>  list) {
        List<T> result = new ArrayList<>();
        for (T entity : list) {
            result.add(entity);
            List<T> children= entity.getChildren();
            if(children == null || children.size()==0){
                continue;
            }
            List<T> entityList = treeToList(children);
            result.addAll(entityList);
        }

        if (result.size() > 0) {
            result.forEach(e->{
                e.setChildren(null);
            });
        }
        return result;
    }




}
