# srb-0608

修改

# 令牌

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

# 垂直分表

1. 什么时候考虑垂直分表？
   - 当一张表中的某些字段不是关键字段 且该字段的内容有很多占据的内存较大我们可以这些字段独立出来形成另一张表，这样我们的性能就会提升
2. 两张表怎么关联起来
   - 使用主键字段关联的方式  一张表是自增主键 另一张表就可以根据自增的主键进行设置。

# 水平分表
