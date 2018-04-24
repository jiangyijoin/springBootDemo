package com.eastcom_sw.etai;

/**
 * Created by JiangYi on 2018/4/13.
 */
public class Student {

    private String name;
    private int age;
    public Student(){}
    public Student(String name,int age){
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public static void main(String[] args){
        Student s = new Student("张三",20);
        change(s);
        System.out.println(s.getName()+";"+s.getAge());
        for(;;){
            byte[][] b = new byte[1024][1024];
        }

    }
    private static void change(Student s){
        s.setName("李四");s.setAge(30);
        s = new Student("王五",40);
    }
}
