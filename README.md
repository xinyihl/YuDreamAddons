## TemplateDevEnv

1. 修改 可配置符文祭坛是否消耗符文
2. 修改 AE存储元件存储种类上限
3. 修改 可配置AE编码样板显示由谁编码
4. 修改 可配置星辉等级上限
5. 修复 更多实用设备2月尘参与星辉合成报错
6. 修复 JEI 在 cleanroom 下最小化报错
7. 修改 允许加速环境科技多方块
8. 修复 FTB 任务在 cleanroom 下鼠标滚轮无效
9. 添加 通过快捷键 `K` 获取物品 id，快捷键 `L` 获取物品 id 列表
10. 修改 可配置实体（包括玩家）在不接触地面时是否受到沉浸工程中电线的伤害
11. 修改 移除了隐身药水的隐身效果（相当于没效果的药水）
12. 修复 更好的建筑之杖放置方块不保存nbt，撤回方块丢失nbt，物品缺少判断tag
13. 添加 更好的建筑之杖副手存在物品时放置副手物品
14. 修改 生物农场在没有存储空间时不输出物品
15. 添加 多人游戏服务器列表美化
16. 添加 电磁发动机发电量计算函数（mods.yudreamaddons.YdUtils.getRandGeneratorEnergy(int x, int y, int z)）
17. 修复 匠魂补充的一个崩服/炸档bug

### Instructions:

* 建立项目`gradlew setupDecompWorkspace`
* 注入标签`gradlew injectTags`
* 运行客户端`gradlew runClient`
* 运行服务端`gradlew runServer`
* 清理构建`gradlew clean build`
* 构建项目`gradlew build`