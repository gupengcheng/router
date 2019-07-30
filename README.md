# router
阿里ARouter开源组件化框架项目实践demo

# How to
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.** Add the dependency

```
dependencies {
	        implementation 'com.github.wellkong:router:V1.0.0'
	}
```

# 前言
做了几个项目的实践，体验到了产品经理的无限添加功能导致我们的项目工程越来越臃肿，这是我们前期的框架设计不好。所以组件化的框架开发是必须的了，各个功能模块都可以分开开发。App模块化的目标是告别结构臃肿，让各个业务变得相对独立，业务模块在组件模式下可以独立开发，而在集成模式下又可以变为依赖包集成到“app壳工程”中，组成一个完整功能的APP。所以下面我们就阿里的ARouter开源组件化框架搭建我们的项目。
## 一、模块化开发的好处

- 公用功能，不用重复开发、修改，代码复用性更强
- 独立运行，提高编译速度，也就提高了开发效率
- 更利于团队开发，不同的人可以独立负责不同的模块
- 独立模块可以采用不同的技术架构，尝试新的技术方案，比如采用新的网络框架，甚至换成Kotlin来开发App

## 二、模块化要解决的问题

- 模块间页面跳转（路由）；
- 模块间事件通信；(EventBus)
- 模块间服务调用；
- 模块的独立运行；
- 模块间页面跳转路由拦截（登录）
## 三、ARouter路由框架
以上模块化需要要解决的问题，2017年阿里开源的路由框架ARouter都有提供解决方案。
官方对这个框架的定义是：一个用于帮助 Android App 进行组件化改造的框架 —— 支持模块间的路由、通信、解耦。
ARouter提供的功能有：

- 支持直接解析标准URL进行跳转，并自动注入参数到目标页面中
- 支持多模块工程使用
- 支持添加多个拦截器，自定义拦截顺序
- 支持依赖注入，可单独作为依赖注入框架使用
- 支持InstantRun
- 支持MultiDex(Google方案)
- 映射关系按组分类、多级管理，按需初始化
- 支持用户指定全局降级与局部降级策略
- 页面、拦截器、服务等组件均自动注册到框架
- 支持多种方式配置转场动画
- 支持获取Fragment
- 完全支持Kotlin以及混编(配置见文末 其他#5)
- 支持第三方 App 加固(使用 arouter-register 实现自动注册)
- 支持生成路由文档
- 提供 IDE 插件便捷的关联路径和目标类

附上ARouter官网地址：[ARouter](https://github.com/alibaba/ARouter/blob/master/README_CN.md)

## 四、用ARouter进行模块化开发
接下来，将会用一个demo介绍如何用ARouter进行模块化开发，demo模块化的整体架构如下：
- app：项目的宿主模块，仅仅是一个空壳，依赖于其他模块，成为项目架构的入口
- baseproject：项目的基类库，每个子模块都依赖共享公用的类和资源，防止公用的功能在不同的模块中有多个实现方式，例如：公用的工具类、自定义控件、mvp/mvc/mvvm等结合网络请求的公共封装等
- commonlib:基础类库，主要是工程依赖的第三方库，所有的第三方库依赖都在这里添加
- arouterlib:ARouter的依赖配置和所有组件模块的路径封装都统一在这里管理
- arouterlib：集中管理所有模块的route
- module_main：闪屏页，登录页，主页等
- module_home：首页模块
![image](129731DE8D1B4377BB7B1CCB242BBB64)
1. 壳工程
壳工程主要用于将各个组件组合起来和做一些工程初始化，初始化包含了后续各个组件会用到的一些库的初始化，也包括ApplicationContext的初始化工作；
2. 基础类库
基础类库主要还是将各个组件中都会用到的一些基础库统一进行封装，例如网络请求、图片缓存、sqllite操作、数据加密等基础类库，这样可以避免各个组件都在自己的组件中单独引用，而且引用的版本可能都不一样，导致整个工程外部库混乱，统一了基础类库后，基础类库保持相对的稳定，这样各个组件对外部库的使用是相对可控的，防止出现一些外部库引出的极端问题，而且这样的话对于库的升级也比较好管理；
3. 基础工程
对于每个组件都有一些是公共的抽象，例如我们工程中自己定义的BaseActivity、BaseFragment、自定义控件等，这些对于每个组件都是一样的，每个组件都基于一样的基础工程开发，一方面可以减少开发工作，另一方面也可以让各个组件的开发人员能够统一架构框架，这样每个组件的技术代码框架看起来都是一样的，也便于后期代码维护和人员互备；
4. 业务模块

## 五、依赖模式与独立运行模式切换
在项目开发中，各个模块可以同时开发，独立运行而不必依赖于宿主app，也就是每个module是一个独立的App，项目发布的时候依赖到宿主app中。各业务模块之间不允许存在相互依赖关系，但是需要依赖基类库。单一模块生成的apk体积也小，编译时间也快，开发效率会高很多，同时也可以独立测试。要实现这样的效果需要对项目做一些配置。
### 1、gradle.properties配置
在项目gradle.properties中需要设置一个开关，用来控制module的编译，如下：
![image](F58CE22C7A1E417C9857803A7548D15F)
```

#配置快关管理模块的单独运行
isModule=false
```
当isModule为false作为依赖库，只能以宿主app启动项目，选择运行模块时其他module前都是红色的X，表示无法运行。
当isModule为true的时候作为单独的模块进行运行，选择其中一个module可以直接运行
### 2、清单文件配置
module清单文件需要配置两个，一个作为独立项目的清单文件，一个作为库的清单文件，以module_home模块为例：
在以module_home模块的路径src-main下新建两个文件夹命名为：buildApp和buildModule在buildApp文件夹下放一个作为依赖库lib的清单文件，buildModule文件夹下放的是独立项目以单独运行工程的清单文件。buildApp作为依赖库的清单文件，和独立项目的清单文件buildModule区别是依赖库的清单文件Application中没有配置入口的Activity，其他都一样
buildApp下的清单文件如下：

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.module_home">

    <application
        android:allowBackup="true"
        android:supportsRtl="true">
        <activity
            android:name="com.module_home.HomeActivity">
        </activity>
    </application>

</manifest>
```
buildModule下的清单文件如下：

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.module_home">

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher_round"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name="com.module_home.HomeActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

    </application>
</manifest>
```
![image](C3D736AF6CD94597A9E8325854029777)
buildApp作为依赖库的清单文件，和独立项目的清单文件buildModule区别是依赖库的清单文件Application中没有配置入口的Activity，其他都一样
### 3、gradle配置
删除了原有的清单文件，然后在分支的gradle文件下配置需要引用的清单文件路径![image](3FA5FC1628014E2684380BF9E0F81C77)
![image](332B231B9BFC4982B48546F79406A7AE)
![image](AE9184D880604E9FA027D41956360F17)
![image](D40A19DF4BD04AB2BA9BC426697F408F)
![image](8FDEB720ADF74166B754EDC77768F681)
整个文件代码如下：

```
if (isModule.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
android {
    compileSdkVersion 28

    defaultConfig {
        minSdkVersion 15
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        sourceSets {
            main {
                if (isModule.toBoolean()) {
                    manifest.srcFile 'src/main/buildModule/AndroidManifest.xml'
                } else {
                    manifest.srcFile 'src/main/buildApp/AndroidManifest.xml'
                }
            }
        }
        //配置ARouter
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [AROUTER_MODULE_NAME: project.getName(),AROUTER_GENERATE_DOC: "enable"]
            }
        }
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.support:appcompat-v7:28.0.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation project(path: ':baseproject')
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.2'
}

```
**上图中，我圈出了ARouter的两个配置，如果需要使用ARouter是所有的分支都必须要添加的代码，否则会报错误找不到组件路径。**
### 4、宿主app配置
![image](87C4DFA663C747468AE9D2CFEC69230A)
### 五、ARouter功能详解
1）gradle中增加库依赖

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:25.1.0'
    testCompile 'junit:junit:4.12'
    //下面两行就是需要添加的ARouter的依赖，arouter-api这个库是arouter的核心库
    //arouter-compiler是annnotation的定义库依赖，对于组件中使用到arouter注解的情况，一定要增加该依赖
    api 'com.alibaba:arouter-api:1.5.0'
    annotationProcessor 'com.alibaba:arouter-compiler:1.2.2'

}
```
2）增加uri路径注解

```
/**
 * https://m.shrb.com/modulea/activity1
 */
@Route(path = "/modulea/activity1")
public class Activity1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
    }
}
```
ARouter管理的页面都需要通过@Route注解进行标注，在注解中需要定义path来表示uri的路径，忘记从哪个版本开始，必须至少使用两级目录，第一级目录代表group，group的概念后面会阐述下，在ARouter中对所有发布的服务做了懒加载，只有group中的任意一个服务第一次被调用的时候才会去一次行把该group下的服务统一加载到内存，这样可以避免启动的时候初始化过多的可能不会用到的组件服务。

3）ARouter初始化

```
if (isDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
    ARouter.openLog();     // 打印日志
    ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
}
ARouter.init(mApplication); // 尽可能早，推荐在Application中初始化

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
```
官方是建议将ARouter的初始化放在自定义的Application中初始化，这样可以避免在某个页面中或者service中初始化，后期资源被回收的问题，导致所有的组件服务全部失效，放在Application中就可以保证ARouter事例在内存中的生命周期和APP保持一致，不会存在资源被误回收的可能；在我后续的例子中为了方便我将初始化放在了demo主Activity中，这只是为了演示，实际项目使用过程中大家一定要放到Application中去初始化，防止app在使用过程中出现一些莫名其妙的问题。

4）页面路由

```
//组件无参数跳转
ARouter.getInstance()
   .build("/modulea/activity1")
   .navigation();
//组件携带参数跳转
 ARouter.getInstance()
   .build("/modulea/activity1")
   .withString("name", "老王")
   .withInt("age", 18)
   .navigation();
```
定义的activity1是和上面调用的activity不在一个module中，我们这里将activity1定义在了moduleA下面，activity1获取参数,可以像spring一样定义Autowired注解，但是这里的Autowired注解可不是spring类库下的自动绑定注解类，而是arouter库下的Autowired类，在activity定义了参数的同名局部变量后就可以在activity中通过ARouter.getInstance().inject(this); 来自动获取到传递的参数，arouter会自动注入到变量中，这样整个过程是不是看起来很简单，很清晰。

```
/**
 * https://m.shrb.com/modulea/activity1?name=老王&age=23
 */
@Route(path = "/modulea/activity1")
public class Activity1 extends Activity {

    @Autowired
    public String name;
    @Autowired
    public int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        //传递参数注入
        ARouter.getInstance().inject(this);

        Log.d("param", name + age);
    }
}
```
### 六. 深入学习ARouter
上面我们介绍了如果简单的引入ARouter并且进行不同组件间的页面路由，下面我们再介绍下ARouter一些高级技能。

1）解析URI中的参数

```
// 为每一个参数声明一个字段，并使用 @Autowired 标注
// URL中不能传递Parcelable类型数据，通过ARouter api可以传递Parcelable对象
@Route(path = "/modulea/activity1")
public class Activity1 extends Activity {
    @Autowired
    public String name;
    @Autowired
    int age;
    @Autowired(name = "girl") // 通过name来映射URL中的不同参数
    boolean boy;
    @Autowired
    TestObj obj;    // 支持解析自定义对象，URL中使用json传递

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ARouter.getInstance().inject(this);

    // ARouter会自动对字段进行赋值，无需主动获取
    Log.d("param", name + age + boy);
    }
}
```

2)定义拦截器
这里所指的拦截器和j2ee中里常说的拦截器是一个概念，就是在处理这个服务前需要处理的动作，也类似jsp中的filter，和okhttp中的拦截器也是一样的概念，可以在拦截器中实现切面公共的功能，这样这些切面公共功能就不会和业务服务代码耦合在一起，是一种比较好的AOP实现，ARouter实现的拦截器也可以对拦截器设置优先级，这样可以对拦截器的处理优先顺序进行处理；但是这个拦截器整体功能还是比较弱，目前的版本实现的是全服务拦截，没有参数可以定义pointcut拦截点，所以如果要对指定页面进行处理只能在

```
// 比较经典的应用就是在跳转过程中处理登陆事件，这样就不需要在目标页重复做登陆检查
// 拦截器会在跳转之间执行，多个拦截器会按优先级顺序依次执行
@Interceptor(priority = 8, name = "测试用拦截器")
public class TestInterceptor implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
    Log.d("Interceptor","拦截器测试");
    callback.onContinue(postcard);  // 处理完成，交还控制权
    // callback.onInterrupt(new RuntimeException("我觉得有点异常"));      
      // 觉得有问题，中断路由流程

    // 以上两种至少需要调用其中一种，否则不会继续路由
    }

    @Override
    public void init(Context context) {
    // 拦截器的初始化，会在sdk初始化的时候调用该方法，仅会调用一次
    }
}
```
3)外部通过URL跳转APP的内部页面
定义SchemaFilterActitity，所有外部来调用本APP的请求，都会先到该SchemeFilterActivity，由该Activity获取uri后再通过Arouter进行转发，这样就可以实现几种效果，1.同一部手机上可以通过自定义url来访问我们app对外暴露的页面并接收外部应用传递过来的值，这样对于集团内应用交叉营销非常方便；2.对外分享的二维码直接扫码后打开app，对于在推广的app，可以实现扫码识别url后直接从外部手机浏览器跳转到我们的app某个页面，支付宝、微信支付都是这样实现在支付的时候唤起原生页面的；

```
public class SchameFilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        ARouter.getInstance().build(uri).navigation();
        finish();
    }
}
```

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.commonlib">

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:supportsRtl="true">
        <activity android:name=".SchameFilterActivity">

            <!-- Schame -->
            <intent-filter>
              <!--自定义host和scheme -->
                <data
                    android:host="m.hop.com"
                    android:scheme="shrb" />

                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
        </activity>
        <activity android:name=".HopWebview"></activity>
    </application>

</manifest>
```
4）定义全局降级页面
相信我们之前遇到过这样的问题，如果跳转的页面不存在，或者调用的组件服务不存在就会报错甚至crash，这里ARouter为我们提供了默认降级服务，一旦路由找不到页面或者服务就会调用该降级service，我们可以在继承自DegradeService类的onLost方法中实现降级需要实现的动作；

```
// 实现DegradeService接口，并加上一个Path内容任意的注解即可
@Route(path = "/xxx/xxx")
public class DegradeServiceImpl implements DegradeService {
    @Override
    public void onLost(Context context, Postcard postcard) {
        Log.d("DegradeService","降级服务启动！");
    }

    @Override
    public void init(Context context) {

    }
}
```
5）组件服务注册与调用

前面我们说了组件间的互相调用都是通过暴露接口，或者服务来实现，笔者原来公司服务之间的互相调用都是通过直接调用依赖接口，所以调用方需要依赖被调用组件的接口，ARouter是将组件的服务对外暴露，调用方直接使用组件暴露的服务uri就可以了，使用起来和spring调用远程服务接口很像，使用起来很简洁；

服务注册

```
// 声明接口,其他组件通过接口来调用服务
public interface HelloService extends IProvider {
    String sayHello(String name);
}

// 实现接口
@Route(path = "/service/hello", name = "测试服务")
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
    return "hello, " + name;
    }

    @Override
    public void init(Context context) {

    }
}
```
服务调用：

```
public class Test {
    @Autowired
    HelloService helloService;

    @Autowired(name = "/service/hello")
    HelloService helloService2;

    HelloService helloService3;

    HelloService helloService4;

    public Test() {
    ARouter.getInstance().inject(this);
    }

    public void testService() {
     // 1. (推荐)使用依赖注入的方式发现服务,通过注解标注字段,即可使用，无需主动获取
     // Autowired注解中标注name之后，将会使用byName的方式注入对应的字段，不设置name属性，会默认使用byType的方式发现服务(当同一接口有多个实现的时候，必须使用byName的方式发现服务)
    helloService.sayHello("monkey0l");
    helloService2.sayHello("monkey01");

    // 2. 使用依赖查找的方式发现服务，主动去发现服务并使用，下面两种方式分别是byName和byType
    helloService3 = ARouter.getInstance().navigation(HelloService.class);
    helloService4 = (HelloService) ARouter.getInstance().build("/service/hello").navigation();
    helloService3.sayHello("monkey01");
    helloService4.sayHello("monkey01");
    }
}
```
### 七.现有项目ARouter改造实践
1）对现有项目进行业务功能拆分；

项目实施组件化一般都是因为项目太复杂或者代码耦合问题比较严重，一修改代码就出现问题，所以首先我们要做的就是先对整个APP的功能进行拆分，对各个组件功能进行边界划分，这个工作其实是整个组件化过程的核心，组件化好不好全看组件功能拆分的好不好；当时我们拆分的过程主要分为几部分：
![image](028A734DA67246BD8E01F27B7D504FC8)
- 1.壳工程
壳工程主要用于将各个组件组合起来和做一些工程初始化，初始化包含了后续各个组件会用到的一些库的初始化，也包括ApplicationContext的初始化工作；
- 2.基础类库
基础类库主要还是将各个组件中都会用到的一些基础库统一进行封装，例如网络请求、图片缓存、sqllite操作、数据加密等基础类库，这样可以避免各个组件都在自己的组件中单独引用，而且引用的版本可能都不一样，导致整个工程外部库混乱，统一了基础类库后，基础类库保持相对的稳定，这样各个组件对外部库的使用是相对可控的，防止出现一些外部库引出的极端问题，而且这样的话对于库的升级也比较好管理；
- 3.基础工程
对于每个组件都有一些是公共的抽象，例如我们工程中自己定义的BaseActivity、BaseFragment、自定义控件等，这些对于每个组件都是一样的，每个组件都基于一样的基础工程开发，一方面可以减少开发工作，另一方面也可以让各个组件的开发人员能够统一架构框架，这样每个组件的技术代码框架看起来都是一样的，也便于后期代码维护和人员互备；
- 4.业务模块
最大的一块体力工作就是业务模块的实现了，上面的几部分都实现以后，剩余的主要体力工作就是实现各个拆分出来的业务模块；

2）基础类库抽离；

对于原先工程中，由于开发人员技术参差不齐、开发赶进度、没有代码复查等原因，导致代码基础类库管理混乱，一个网络请求居然都有好几个外部库，在组件化过程中我们对常用的几个公共类库进行了统一，并且都封装在了一个基础类库中。

3）基础工程抽离；

对于基础工程抽离其实比较简单，因为我们原先的工程对于公共类这已经抽象的比较好了，主要是在基础工程中抽象出了BaseActiviry、BaseFragment、BaseApplication、BaseViewModel等公共类，在每个Base类中都已经定义了一些必须要实现的抽象类和已经定义好的基础函数功能，这样以后每个定义的Activity或者Fragment都继承了这些Base类的基础功能，能够减少很多公共代码的开发工作，也可以在基础类中实现统一异常处理，统一消息捕获，切面拦截等等，只要是组件中公共的功能都可以在这里实现；

4）壳工程开发；

壳工程其实比较简单，一般壳工程中主要承担了项目初始化的工作，在壳的Application中需要对后续其它组件用到的全局库进行统一初始化，也要承担一些例如版本检测、配置文件加载、刷新、组件加载、热更新等工作。壳工程主要做的都是些基础的脏活累活，壳工程开发好后，就可以进行业务功能开发了，每次开发单模块功能的时候，可以在壳工程中只引用自己模块需要的功能进行开发编译调试了，不需要全部组件引用，这样可以大大缩减每次编译的时间，如果打开instance run那么编译的速度就更快了；

5）业务功能开发；

每个组件的业务功能开发这里就没什么好说的了，主要就是堆人去实施；

![image](37322347861B44B59EBB8C4DF381EA27)
如上图我们的依赖关系为：

所有的module都依赖基础工程baseproject,baseproject依赖公共三方库公共包commonlib,commonlib依赖arouterlibrary。

### 八、Android Butterknife在library组件化模块中的使用问题
**1、问题**

当项目中有多module时，在使用Butterknife的时候会发现在library模块中使用会出问题。当library模块中的页面通过butterknife找id的时候，就会报错，提示@BindView的属性必须是一个常数，也就是说library module编译的时候，R文件中所有的数据并没有被加上final，也就是R文件中的数据并非常量。

2、**解决步骤**

I 首先在项目的总build.gradle中添加classpath

```
classpath 'com.jakewharton:butterknife-gradle-plugin:8.2.1'
```
![image](6B0F610C249C4CDAACAAEF9FDD6A8A7D)
II 在library中build.gradle中引入插件

```
apply plugin: 'com.jakewharton.butterknife'
```
![image](6BB58ECC93CB462BA3B2BF1AEC9B884F)
III 在library中build.gradle中dependencies添加依赖

```
compile "com.jakewharton:butterknife:8.5.1"
annotationProcessor "com.jakewharton:butterknife-compiler:8.5.1"
```
**3、butterknife在library activity中的使用和注意事项**

1、用R2代替R findviewid

```
   @BindView(R2.id.textView)
    TextView textView;
    @BindView(R2.id.button1)
    Button button1;
    @BindView(R2.id.image)
    ImageView image;
```
2、在click方法中同样使用R2，但是找id的时候使用R

```
 @OnClick({R2.id.textView, R2.id.button1, R2.id.button2, R2.id.button3, R2.id.image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textView:
                break;
            case R.id.button1:
                break;
            case R.id.image:
                break;
        }
    }
```
3、特别注意library中switch-case的使用，在library中是不能使用switch- case 找id的，解决方法就是用if-else代替

```
@OnClick({R2.id.textView, R2.id.button1, R2.id.button2, R2.id.button3, R2.id.image})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.textView) {

        } else if (i == R.id.button1) {

        } else if (i == R.id.image) {

        }
    }
```

至此就实现了我们项目的组件化开发了。


## 题外问题
配置环境切换内容的改变

## 1、配置 gradle.properties

```
BASE_API_DEBUG=\"http://api.android.com\"
BASE_API_PRODUCT=\"http://api.android.test\"
BAIDU_APPKEY_VALUE_DEBUG=xrAslSGF7xjSLI2kFFgQb1Dl
BAIDU_APPKEY_VALUE_PRODUCT=aMfi0c1f3oeefjmMpmEEeGPs
```
## 2、配置项目的 build.gradle 文件

```
buildTypes {
        release {
            buildConfigField("String", "BASE_API", project.BASE_API_PRODUCT)
            manifestPlaceholders = [BAIDU_APPKEY_VALUE: project.BAIDU_APPKEY_VALUE_PRODUCT]
      }
        debug {
            buildConfigField("String", "BASE_API", project.BASE_API_DEBUG)
            manifestPlaceholders = [BAIDU_APPKEY_VALUE: project.BAIDU_APPKEY_VALUE_DEBUG]
        }
    }
```
buildConfigField("String", "BASE_API", project.BASE_API_PRODUCT)

**BASE_API_PRODUCT 和 BASE_API_DEBUG 就是我们在 gradle.properties 中定义的正式环境和测试环境的 API 根地址。这一行代码编译后会在 BuildConfig.java 文件中生成下面的代码。**

```
public static final String BASE_API = "http://api.android.com";  正式环境
public static final String BASE_API = "http://api.android.test";  测试环境
```
这样我们就可以直接调用 BuildConfig.BASE_API 来使用 API 根地址了。

```
manifestPlaceholders = [BAIDU_APPKEY_VALUE: project.BAIDU_APPKEY_VALUE_PRODUCT]
```
**BAIDU_APPKEY_VALUE_PRODUCT 是我们在 gradle.properties 中定义的百度地图 APP KEY， 当然了，如果你有多个 SDK 的 KEY , 可以这样写：**

```
 manifestPlaceholders = [BAIDU_APPKEY_VALUE     : project.BAIDU_APPKEY_VALUE_PRODUCT
                         , GETUI_APPID_VALUE    : project.GETUI_APPID_VALUE_PRODUCT
                         , GETUI_APPKEY_VALUE   : project.GETUI_APPKEY_VALUE_PRODUCT
                         , GETUI_APPSECRET_VALUE: project.GETUI_APPSECRET_VALUE_PRODUCT
                         , QQ_SHARE_APP_ID_VALUE: project.QQ_SHARE_APP_ID_PRODUCT]
```
顾名思义， manifestPlaceholders 就是配置在 manifest 中引用的变量的。
配置完之后，我们就可以在 manifest 中使用了。

```
  <meta-data
           android:name="com.baidu.lbsapi.API_KEY"
            android:value="${BAIDU_APPKEY_VALUE}" />
     <meta-data
           android:name="PUSH_APPID"
           android:value="${GETUI_APPID_VALUE}" />
     <meta-data
           android:name="PUSH_APPKEY"
           android:value="${GETUI_APPKEY_VALUE}" />
     <meta-data
           android:name="PUSH_APPSECRET"
           android:value="${GETUI_APPSECRET_VALUE}" />
```
**记录**

**环境的变量BuildConfig**
请求的URL，第三方SDK的密钥等信息，一般会作为静态变量，存放于一个所谓的Constants类里面，需要引用的时候直接调用这个类即可。但是就像文章开头所说的，变量太多的时候，修改起来就会比较麻烦。我们可以利用一个叫BuildConfig的类，这个类会在APP编译时生成，我们经常利用BuildConfig.DEBUG==true来识别当前是否为调试环境。根据这个思路，那么有没有办法将我们的URL，密钥等信息存放于BuildConfig呢？利用gradle的特性，即可实现。

**buildTypes**

在app module 的build.gradle中修改buildTypes节点：分别对应debug调试环境、areaTest测试环境、release正式环境，debug环境可与devTest环境配置一致。

**applicationIdSuffix**

在debug或者测试环境下设置applicationIdSuffix，即可更改APP的包名，以下代码会自动将当前包名更改为默认的包名后加上.test

```
debug{
applicationIdSuffix:".test"
}
```
**it.buildConfigField**

如下面的代码块，it.buildConfigField 主要用来配置BuildConfig的静态变量，写入成功后通过BuildConfig调用，那么在三个环境内配置不同的变量属性即可区分三个环境。

**manifestPlaceholders**

AndroidManifest文件下的占位标识

AndroidManifest下的节点设置属性=${PAKAGE_NAME}，

```
<meta-data
    android:name="UMENG_CHANNEL"
        android:value="${PAKAGE_NAME}" 
    />

```
那么在manifestPlaceholders配置对应字段即可

```

 manifestPlaceholders = [ PAKAGE_NAME:"com.gzkit.sample"]
```
在编译打包的过程中，会将AndroidManifest的占位标识自动替换。对于一些sdk只能在AndroidManifest去设置key的情况，那么这种方式就可以用于区分各个环境了。

```
buildTypes {    
    release {
            it.buildConfigField 'String', 'BASE_URL', "\"http://sample.com:8080/\"";
                it.buildConfigField 'String', 'API_URL', "\"dlpmobile/mobile/\"";
                it.buildConfigField 'String', 'UPLOAD_FILE_URL', "\"dlpfilesystem/utils/imageService/\"";
                it.buildConfigField 'String', 'E_CMS_URL', "\"dlpecms/ecms/\"";        it.buildConfigField 'String', 'JPUSH_APPKEY',"\"adadasd\"";
                it.buildConfigField 'String', 'MIPUSH_APPKEY',"\"2123\"";
                it.buildConfigField 'String', 'MIPUSH_APP_ID',"\"312312\"";
                it.buildConfigField 'String', 'BUGLY_APP_ID',"\"312312\"";
                manifestPlaceholders = [
                        APP_NAME:"测试用例",
                                PAKAGE_NAME:"com.gzkit.sample",
                                JPUSH_PERMISSION:"com.gzkit.sample.permission.JPUSH_MESSAGE",
                                MIPUSH_PERMISSION:"com.gzkit.sample.permission.MIPUSH_RECEIVE",
                                UMENG_CHANNEL: "sample",
                                JPUSH_PKGNAME: "com.gzkit.sample",
                                JPUSH_APPKEY : "adads21312", //JPush上注册的包名对应的appkey.
                                JPUSH_CHANNEL: "tencent" //用户渠道统计的渠道名称
                        ]        signingConfig signingConfigs.config        minifyEnabled false        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'    }
        debug {
            applicationIdSuffix ".test"
                 it.buildConfigField 'String', 'BASE_URL', "\"http://172.16.17.152:8080/\"";
                 it.buildConfigField 'String', 'API_URL', "\"dlpmobile/mobile/\"";
                it.buildConfigField 'String', 'UPLOAD_FILE_URL', "\"dlpfilesystem/utils/imageService/\"";
                it.buildConfigField 'String', 'E_CMS_URL', "\"dlpecms/ecms/\"";
                it.buildConfigField 'String', 'JPUSH_APPKEY',"\"adasdasd\"";
                it.buildConfigField 'String', 'MIPUSH_APPKEY',"\"dadasd\"";
                it.buildConfigField 'String', 'MIPUSH_APP_ID',"\"dasdasd\"";
                it.buildConfigField 'String', 'BUGLY_APP_ID',"\"dasdasd\"";
                manifestPlaceholders = [
                        APP_NAME:"sample测试版",
                                PAKAGE_NAME:"com.gzkit.sample.test",
                                JPUSH_PERMISSION:"com.gzkit.dlp.sample.test.permission.JPUSH_MESSAGE",
                                MIPUSH_PERMISSION:"com.gzkit.dlp.sample.test.permission.MIPUSH_RECEIVE",
                                UMENG_CHANNEL: "sample",
                                JPUSH_PKGNAME: "com.gzkit.sample.test",
                                JPUSH_APPKEY : "dasdasdasd", //JPush上注册的包名对应的appkey.
                                JPUSH_CHANNEL: "tencent" //用户渠道统计的渠道名称
                        ]    }
        devTest{        applicationIdSuffix ".test"
                it.buildConfigField 'String', 'BASE_URL', "\"http://172.16.17.152:8080/\"";
                it.buildConfigField 'String', 'API_URL', "\"dlpmobile/mobile/\"";
                it.buildConfigField 'String', 'UPLOAD_FILE_URL', "\"dlpfilesystem/utils/imageService/\"";
                it.buildConfigField 'String', 'E_CMS_URL', "\"dlpecms/ecms/\"";
                it.buildConfigField 'String', 'JPUSH_APPKEY',"\"dadasdasd\"";
                it.buildConfigField 'String', 'MIPUSH_APPKEY',"\"dasdadasd\"";
                it.buildConfigField 'String', 'MIPUSH_APP_ID',"\"dasdasd\"";
                it.buildConfigField 'String', 'BUGLY_APP_ID',"\"dadasd\"";
                manifestPlaceholders = [
                        APP_NAME:"sample测试版",
                                PAKAGE_NAME:"com.gzkit.sample.test",
                                JPUSH_PERMISSION:"com.gzkit.sample.test.permission.JPUSH_MESSAGE",
                                MIPUSH_PERMISSION:"com.gzkit.sample.test.permission.MIPUSH_RECEIVE",
                                UMENG_CHANNEL: "sample",
                                JPUSH_PKGNAME: "com.gzkit.sample.test",
                                JPUSH_APPKEY : "dasdasdasd", //JPush上注册的包名对应的appkey.
                                JPUSH_CHANNEL: "tencent" //用户渠道统计的渠道名称
                        ]        signingConfig signingConfigs.config        minifyEnabled false    }}
 

```
发布正式环境时，选择release编译打包即可，测试环境则选择devTest
