##CI/CD环境的搭建介绍
###Travis CI是在软件开发领域中的一个在线的，分布式的持续集成服务，用来构建及测试在GitHub托管的代码。

###我们的Java项目以 Git + Maven 作为版本和依赖管理工具。

###PART 1 什么是CD&CI
----------
###ONE：持续集成

持续集成(Continuous integration)是一种软件开发实践，即团队开发成员经常集成它们的工作，通过每个成员每天至少集成一次也就意味着每天可能会发生多次集成
每次集成都通过自动化的构建（包括编译，发布，自动化测试）来验证，从而尽早地发现集成错误

![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%876.png)

###TWO:持续部署

是通过自动化的构建、测试和部署循环来快速交付高质量的产品
某种程度上代表了一个开发团队工程化的程度，毕竟快速运转的互联网公司人力成本会高于机器，投资机器优化开发流程化相对也提高了人的效率

![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%877.png)

###PART 2 为什么要用CI&CD
----------
###ONE：无CD&CI
####本地机器上写代码
####在命令行输入 npm run unit，查看单元测试结果
####提交代码，push 到 git 远程仓库
####登录测试服务器，拉取代码，执行 npm run build，构建项目
####如果测试服务器是基于 pm2 的 proxy server，还需要重启 server

###TWO：引入CD&CI
####本地机器上写代码
####提交代码，push 到 git 远程仓库
####git hook 触发 jenkins 的构建 job （自动）
####jenkins job 中拉取项目代码，运行 npm run unit 和 npm run build，如果失败，发送邮件通知相关人。（自动）
####jenkins job 中执行测试服务器的部署脚本 （自动）


###PART 3 搭建步骤
----------
###STEP1:访问官方网站 travis-ci.org，点击右上角的个人头像，使用 Github 账户登入 Travis CI

![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%871.png)

###STEP2: 为Travis Ci 添加配置文档 [官方教程-java](https://docs.travis-ci.com/user/languages/java/)

- .travis.yml:

      [![Selenium Test Status](https://img-blog.csdn.net/20170820170604698?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars)
      
假如只用这一行配置，指定的测试环境是默认的：

[![Selenium Test Status](https://img-blog.csdn.net/20170820171512784?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars)

在此之前，我们要处理Maven的依赖:

```js
mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
```
###STEP3:检测配置文件是否正常
测试页面-----[Travis WebLint](https://lint.travis-ci.org/znc/znc)

[![Selenium Test Status](https://img-blog.csdn.net/20170820171550481?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars)
###STEP4:与GitHub集成
通过GitHub账户登录Travis CI之后，跳转至自己账户下设置仓库：打开需要设置的仓库

[![Selenium Test Status](https://img-blog.csdn.net/20170820172823604?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars)

如果是组织需要先赋予权限：（点击页面中的添加组织）

[![Selenium Test Status](https://img-blog.csdn.net/20170820172951670?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 

跳转到此处，grant赋予权限。

将仓库打开后，以后的每次push / PR操作都会发送到Travis CI进行测试执行mvn test -B

也可以指定构建脚本，例如：script: mvn package

如果项目结构标准，pom.xml在根目录下，执行就会一切正常。但是，如果pom.xml在子目录下，就需要修改install脚本：

![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%872.png)</br>
![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%873.png)</br>
![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%874.png)


###STEP5:Test Service 测试
进入自己的GItHub，进入项目settings
[![Selenium Test Status](https://img-blog.csdn.net/20170820175158760?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 

[![Selenium Test Status](https://img-blog.csdn.net/20170820175549679?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 

进入自己的Travis CI账户，点击：查看自动测试状态

[![Selenium Test Status](https://img-blog.csdn.net/20170820175744235?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 

[![Selenium Test Status](https://img-blog.csdn.net/20170820180728359?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 
###STEP5:将Travis Ci的结果添加至README.md

[![Selenium Test Status](https://img-blog.csdn.net/20170820181103886?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 

按照提示，将链接 
添加至README.md

```js
[![Build Status](https://travis-ci.org/EJOSystem-core/EJOS.svg?branch=master)](https://travis-ci.org/EJOSystem-core/EJOS)
```

[![Selenium Test Status](https://img-blog.csdn.net/20170820215737402?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGltbzExNjAxMzkyMTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)](https://saucelabs.com/u/handlebars) 

###PART 4 个人思考
----------
###ONE：CI&CD做法的核心思想

事实上难以做到事先完全了解完整的、正确的需求，那么就干脆一小块一小块的做，并且加快交付的速度和频率，使得交付物尽早在下个环节得到验证。早发现问题早返工。

![](https://github.com/TactfulYuu/CI-CD/blob/patch-1/image/%E5%9B%BE%E7%89%875.png)

###TWO:尽早测试，不要积压代码



