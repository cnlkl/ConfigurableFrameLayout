package cn.lkllkllkl.conficurableframelayoutsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.lkllkllkl.conficurableframelayout.DragableImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DragableImageView dragableImageView1 =
                (DragableImageView) findViewById(R.id.dragable_image_view_1);
        DragableImageView dragableImageView2 =
                (DragableImageView) findViewById(R.id.dragable_image_view_2);
        GlideApp.with(this)
                .load(R.drawable.black)
                .into(dragableImageView1);
        GlideApp.with(this)
                .load(R.drawable.cat)
                .into(dragableImageView2);
    }
}
