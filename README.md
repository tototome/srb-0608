# srb-0608

修改


# 垂直分表

1. 什么时候考虑垂直分表？
   - 当一张表中的某些字段不是关键字段 且该字段的内容有很多占据的内存较大我们可以这些字段独立出来形成另一张表，这样我们的性能就会提升
2. 两张表怎么关联起来
   - 使用主键字段关联的方式  一张表是自增主键 另一张表就可以根据自增的主键进行设置。

# 水平分表

# 数据库的锁

- update语句使用的是行锁
- mysql里面读写是可以同时进行的，写写是互斥操作的
- 锁升级
- 行锁
- 表锁
- 乐观锁
  - 再操作资源之前认为肯定会没有竞争，不提前上锁，只有操作的一瞬间通过version判断资源是否正常。
  - 是一个逻辑锁
  - select version
  - update table set ...
- 悲观锁
  - 再操作资源之前认定肯定有竞争先加上锁

顶级接口  中间抽象类 底层实现类 框架的结构

插件的使用

分页插件 


