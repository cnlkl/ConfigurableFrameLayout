# ConfigurableFrameLayout
A custom FrameLayout that allows you to drag exchange ImageView picture 

[introduction](http://www.jianshu.com/p/dbc0c94434c1)

## ScreenShot
![screenshot](https://raw.githubusercontent.com/cnlkl/ConfigurableFrameLayout/master/screenshots/drag_img.gif)

## Add dependency
### **Step 1.** Add the JitPack repository to your build file
### Add it in your root build.gradle at the end of repositories:   

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### **Step 2.** Add the dependency
```gradle
dependencies {
        compile 'com.github.cnlkl:ConfigurableFrameLayout:1.0-alpha'
}
```
## How to Use

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DraggableImageView draggableImageView1 =
                (DraggableImageView) findViewById(R.id.draggable_image_view_1);
        DraggableImageView draggableImageView2 =
                (DraggableImageView) findViewById(R.id.draggable_image_view_2);
        DraggableImageView draggableImageView3 =
                (DraggableImageView) findViewById(R.id.draggable_image_view_3);
        // Use Glide to load image
        GlideApp.with(this)
                .load(R.drawable.black)
                .into(draggableImageView1);
        GlideApp.with(this)
                .load(R.drawable.cat)
                .into(draggableImageView2);
        GlideApp.with(this)
                .load(R.drawable.cat)
                .into(draggableImageView3);
    }
}
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<cn.lkllkllkl.configurableframelayout.ConfigurableFrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    tools:context="cn.lkllkllkl.configurableframelayoutsample.MainActivity">

    <cn.lkllkllkl.configurableframelayout.DraggableImageView
        android:background="@color/gray"
        android:id="@+id/draggable_image_view_1"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginLeft="90dp"
        android:layout_marginTop="30dp"
        app:max_scale="4"
        app:boundary_bottom="true"
        app:trigger_distance="100dp"
        />


    <cn.lkllkllkl.configurableframelayout.DraggableImageView
        android:background="@color/gray"
        android:id="@+id/draggable_image_view_2"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_marginTop="270dp"
        android:layout_marginLeft="50dp"
        app:max_scale="4"
        app:boundary_top="true"
        app:boundary_right="true"
        app:trigger_distance="3dp"/>

    <cn.lkllkllkl.configurableframelayout.DraggableImageView
        android:background="@color/gray"
        android:id="@+id/draggable_image_view_3"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_marginTop="290dp"
        android:layout_marginLeft="220dp"
        app:max_scale="4"
        app:boundary_left="true"
        app:boundary_top="true"
        app:trigger_distance="3dp"/>
</cn.lkllkllkl.configurableframelayout.ConfigurableFrameLayout>

```
