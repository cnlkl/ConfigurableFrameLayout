package cn.lkllkllkl.conficurableframelayoutsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.lkllkllkl.conficurableframelayout.DraggableImageView;

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
