### 简介
*  library-lis是仿照微信本地图片选择的效果实现的一个库，包括图片选择和图片浏览效果可参考微信。
*  开发者可以根据自己的需求灵活的定制自己的topLayout,bottomLayout。

###library-lisde简易UML类图
![图片1](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic1.png)
![图片2](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic2.png)

###demo运行效果图
![图片3](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic3.png)
![图片4](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic4.png)
![图片5](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic5.png)
![图片6](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic6.png)
![图片7](https://github.com/cocolove2/LISDemo/blob/master/app/screenshot/pic7.png)

###[图片选择demo参考](https://github.com/cocolove2/LISDemo/blob/master/app/src/main/java/com/lisdemo/MainActivity.java)

###[图片预览demo参考](https://github.com/cocolove2/LISDemo/blob/master/app/src/main/java/com/lisdemo/MyPreActivity.java)
* 图片预览的支持是使用隐式跳转。
```
  <intent-filter>
                <action android:name="com.cocolover2.lis.ACTION_preview" />
                //your packagename+".category.PREVIEW"
                <category android:name="com.lisdemo.category.PREVIEW" />
                <category android:name="android.intent.category.DEFAULT" />
  </intent-filter>
```


