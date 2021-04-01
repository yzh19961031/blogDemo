package com.yzh.stack;

/**
 * 局部变量表测试
 * javap -v StackTest1.class
 * Classfile /Users/yuanzhihao/git/blogDemo/jvm/target/classes/com/yzh/stack/StackTest1.class
 *   Last modified 2021-3-31; size 715 bytes
 *   MD5 checksum 6499527f3f3b68c581e313744470c7c9
 *   Compiled from "StackTest1.java"
 * public class com.yzh.stack.StackTest1
 *   minor version: 0
 *   major version: 52
 *   flags: ACC_PUBLIC, ACC_SUPER
 * Constant pool:
 *    #1 = Methodref          #9.#34         // java/lang/Object."<init>":()V
 *    #2 = Long               20l
 *    #4 = Double             30.0d
 *    #6 = Class              #35            // com/yzh/stack/StackTest1$Demo
 *    #7 = Methodref          #6.#36         // com/yzh/stack/StackTest1$Demo."<init>":(Lcom/yzh/stack/StackTest1$1;)V
 *    #8 = Class              #37            // com/yzh/stack/StackTest1
 *    #9 = Class              #38            // java/lang/Object
 *   #10 = Class              #39            // com/yzh/stack/StackTest1$1
 *   #11 = Utf8               InnerClasses
 *   #12 = Utf8               Demo
 *   #13 = Utf8               <init>
 *   #14 = Utf8               ()V
 *   #15 = Utf8               Code
 *   #16 = Utf8               LineNumberTable
 *   #17 = Utf8               LocalVariableTable
 *   #18 = Utf8               this
 *   #19 = Utf8               Lcom/yzh/stack/StackTest1;
 *   #20 = Utf8               main
 *   #21 = Utf8               ([Ljava/lang/String;)V
 *   #22 = Utf8               args
 *   #23 = Utf8               [Ljava/lang/String;
 *   #24 = Utf8               i
 *   #25 = Utf8               I
 *   #26 = Utf8               j
 *   #27 = Utf8               J
 *   #28 = Utf8               k
 *   #29 = Utf8               D
 *   #30 = Utf8               demo
 *   #31 = Utf8               Lcom/yzh/stack/StackTest1$Demo;
 *   #32 = Utf8               SourceFile
 *   #33 = Utf8               StackTest1.java
 *   #34 = NameAndType        #13:#14        // "<init>":()V
 *   #35 = Utf8               com/yzh/stack/StackTest1$Demo
 *   #36 = NameAndType        #13:#40        // "<init>":(Lcom/yzh/stack/StackTest1$1;)V
 *   #37 = Utf8               com/yzh/stack/StackTest1
 *   #38 = Utf8               java/lang/Object
 *   #39 = Utf8               com/yzh/stack/StackTest1$1
 *   #40 = Utf8               (Lcom/yzh/stack/StackTest1$1;)V
 * {
 *   public com.yzh.stack.StackTest1();
 *     descriptor: ()V
 *     flags: ACC_PUBLIC
 *     Code:
 *       stack=1, locals=1, args_size=1
 *          0: aload_0
 *          1: invokespecial #1                  // Method java/lang/Object."<init>":()V
 *          4: return
 *       LineNumberTable:
 *         line 9: 0
 *       LocalVariableTable:
 *         Start  Length  Slot  Name   Signature
 *             0       5     0  this   Lcom/yzh/stack/StackTest1;
 *
 *   public static void main(java.lang.String[]);
 *     descriptor: ([Ljava/lang/String;)V
 *     flags: ACC_PUBLIC, ACC_STATIC
 *     Code:
 *       stack=3, locals=7, args_size=1
 *          0: bipush        10
 *          2: istore_1
 *          3: ldc2_w        #2                  // long 20l
 *          6: lstore_2
 *          7: ldc2_w        #4                  // double 30.0d
 *         10: dstore        4
 *         12: new           #6                  // class com/yzh/stack/StackTest1$Demo
 *         15: dup
 *         16: aconst_null
 *         17: invokespecial #7                  // Method com/yzh/stack/StackTest1$Demo."<init>":(Lcom/yzh/stack/StackTest1$1;)V
 *         20: astore        6
 *         22: return
 *       LineNumberTable:
 *         line 13: 0
 *         line 15: 3
 *         line 17: 12
 *         line 18: 22
 *       LocalVariableTable:
 *         Start  Length  Slot  Name   Signature
 *             0      23     0  args   [Ljava/lang/String;
 *             3      20     1     i   I
 *             7      16     2     j   J
 *            12      11     4     k   D
 *            22       1     6  demo   Lcom/yzh/stack/StackTest1$Demo;
 * }
 * SourceFile: "StackTest1.java"
 * InnerClasses:
 *      static #10; //class com/yzh/stack/StackTest1$1
 *
 * @author yuanzhihao
 * @since 2021/3/31
 */
public class StackTest1 {

    public static void main(String[] args) {
        // 除了64位长度的基本数据类型占用两个槽位 其他只占用一个槽位
        int i = 10;
        // long double 占两个槽位
        long j = 20; double k = 30.0;
        // 引用数据类型占一个槽位
        Demo demo = new Demo();
    }


    private void test1() {
        // 这边注意 非静态方法Slot的索引为0的位置是this 代表当前类对象
        int k = 20;
    }

    private static class Demo {

    }
}
