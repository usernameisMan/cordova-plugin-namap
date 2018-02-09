# cordova-plugin-namap
插入原生IOS，Android高德地图cordova插件完全通过js调用原生SDK功能,能够帮助实现类似，滴滴，OFO，等底部是地图的软件,可以辅助实现比原生更快捷的UI，使用webView方便web开发人员更改和美化ui。

目前只有android的版本并且，API调用没有完善还在进行中。

## 如何配置 

>设置高德Key在根目录plugin.xml value替换为你的Key
```xml
<meta-data android:name="com.amap.api.v2.apikey" android:value="c589d3febc9665e14592985633391399"/>
```

## 如何调用
>cordova中调用Demo的方式 

```javascript
// index.js
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },
    onDeviceReady: function() {
        this.initAmpa();
    },
    initAmpa:function(){
        //获取调用
        window.cordova_plugins_namap.getMap(app.success,app.error)
    },
    success:function(){
        console.log('地图调用成功')
    },
    error:function(){
        alert('失败调用地图')
    }
};

app.initialize();
```
>ionic3中调用Demo的方式 