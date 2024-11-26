## TemplateDevEnv

1. 符文祭坛是否消耗符文
2. AE存储元件存储种类上限
3. AE编码样板显示由谁编码
4. 星辉等级上限修改
5. 更多实用设备2月尘参与星辉合成报错
6. JEI 在 cleanroom 下最小化报错
7. 允许加速环境科技多方块
8. FTB 任务在 cleanroom 下鼠标滚轮无效
9. 通过快捷键 `K` 获取物品 id
10. 通过快捷键 `L` 获取物品 id 列表
11. 防止实体（包括玩家）在不接触地面时受到沉浸工程中电线的伤害

### Instructions:

1. In the local repository, run the command `gradlew setupDecompWorkspace`
2. Open the project folder in IDEA.
3. Right-click in IDEA `build.gradle` of your project, and select `Link Gradle Project`, after completion, hit `Refresh All` in the gradle tab on the right.
4. Run `gradlew runClient` and `gradlew runServer`, or use the auto-imported run configurations in IntelliJ like `1. Run Client`.