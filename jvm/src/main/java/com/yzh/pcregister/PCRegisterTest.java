package com.yzh.pcregister;

/**
 * pc寄存器测试
 *
 * 使用 javap -v PCRegisterTest.class命令可以查看生成的字节码文件
 * Classfile /Users/yuanzhihao/git/blogDemo/jvm/target/classes/com/yzh/pcregister/PCRegisterTest.class
 *   Last modified 2021-3-26; size 491 bytes
 *   MD5 checksum e32606b8d1f98abaa67014a4e021a5dd
 *   Compiled from "PCRegisterTest.java"
 * public class com.yzh.pcregister.PCRegisterTest
 *   minor version: 0
 *   major version: 52
 *   flags: ACC_PUBLIC, ACC_SUPER
 * Constant pool:
 *    #1 = Methodref          #3.#21         // java/lang/Object."<init>":()V
 *    #2 = Class              #22            // com/yzh/pcregister/PCRegisterTest
 *    #3 = Class              #23            // java/lang/Object
 *    #4 = Utf8               <init>
 *    #5 = Utf8               ()V
 *    #6 = Utf8               Code
 *    #7 = Utf8               LineNumberTable
 *    #8 = Utf8               LocalVariableTable
 *    #9 = Utf8               this
 *   #10 = Utf8               Lcom/yzh/pcregister/PCRegisterTest;
 *   #11 = Utf8               main
 *   #12 = Utf8               ([Ljava/lang/String;)V
 *   #13 = Utf8               args
 *   #14 = Utf8               [Ljava/lang/String;
 *   #15 = Utf8               i
 *   #16 = Utf8               I
 *   #17 = Utf8               j
 *   #18 = Utf8               k
 *   #19 = Utf8               SourceFile
 *   #20 = Utf8               PCRegisterTest.java
 *   #21 = NameAndType        #4:#5          // "<init>":()V
 *   #22 = Utf8               com/yzh/pcregister/PCRegisterTest
 *   #23 = Utf8               java/lang/Object
 * {
 *   public com.yzh.pcregister.PCRegisterTest();
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
 *             0       5     0  this   Lcom/yzh/pcregister/PCRegisterTest;
 *
 *   public static void main(java.lang.String[]);
 *     descriptor: ([Ljava/lang/String;)V
 *     flags: ACC_PUBLIC, ACC_STATIC
 *     Code:
 *       stack=2, locals=4, args_size=1
 *          0: bipush        10
 *          2: istore_1
 *          3: bipush        20
 *          5: istore_2
 *          6: iload_1
 *          7: iload_2
 *          8: iadd
 *          9: istore_3
 *         10: return
 *       LineNumberTable:
 *         line 12: 0
 *         line 13: 3
 *         line 14: 6
 *         line 15: 10
 *       LocalVariableTable:
 *         Start  Length  Slot  Name   Signature
 *             0      11     0  args   [Ljava/lang/String;
 *             3       8     1     i   I
 *             6       5     2     j   I
 *            10       1     3     k   I
 * }
 * SourceFile: "PCRegisterTest.java"
 *
 * @author yuanzhihao
 * @since 2021/3/26
 */
public class PCRegisterTest {

    public static void main(String[] args) {
        int i = 10;
        int j = 20;
        int k = i + j;
    }
}
