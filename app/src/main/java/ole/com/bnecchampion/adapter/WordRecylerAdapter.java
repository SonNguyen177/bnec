package ole.com.bnecchampion.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ole.com.bnecchampion.R;
import ole.com.bnecchampion.modal.WordModel;

/**
 * Created by nguye on 3/31/2016.
 */
public class WordRecylerAdapter extends RecyclerView.Adapter<WordRecylerAdapter.ViewHolder> {

    private Context context;
    // Define listener member variable
    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    List<WordModel> list;

    public WordRecylerAdapter(Context context, List<WordModel> data) {
        this.context = context;
        list = data;
    }

    public void setData(List<WordModel> data){
        list = data;
    }

    public List<WordModel> getData(){
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View chapterView = inflater.inflate(R.layout.item_word, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(chapterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        WordModel obj = list.get(position);

        // Set item views based on the data model
        holder.tvWorld.setText(obj.getWord());
        holder.tvPronoun.setText(obj.getPronoun());
        holder.tvType.setText(obj.getDescription());


        ShapeDrawable leafBackground = new ShapeDrawable();

        int textColor = Color.WHITE;
        int strokeWidth = 3; // dp
        // convert to px
        strokeWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, context.getResources().getDisplayMetrics());
        int strokeColor = Color.WHITE;
        int fillColor;

// The corners are ordered top-left, top-right, bottom-right,
// bottom-left. For each corner, the array contains 2 values, [X_radius,
// Y_radius]
        float radius = 20; //dp
        // convert to px
        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, context.getResources().getDisplayMetrics());
       // float[] outerR  = {0, 0, radius, radius, 0, 0, radius, radius};

        float topLeft, topRight, bottomLeft, bottomRight;
        topLeft = topRight = bottomLeft =  bottomRight =0;

        switch (position){
            case 0:
                topRight = bottomLeft = radius;

                    break;
            case 1:
                topLeft = bottomRight = radius;
                break;
            case 2:
                topLeft = bottomRight = radius;
                break;
            case 3:

                topRight = bottomLeft = radius;
                break;
        }

        switch (obj.getStatus()){
            case 0:
               // holder.panelWord.setBackgroundColor(Color.parseColor("#ffffff"));
                textColor = Color.DKGRAY;
                fillColor = Color.parseColor("#ffffff");
                break;
            case 1:
                fillColor = ContextCompat.getColor(this.context, R.color.mainColor);
                break;
            case 2:
                fillColor = ContextCompat.getColor(this.context,R.color.successColor);
                break;
            case 3:
                fillColor = ContextCompat.getColor(this.context,R.color.failColor);
                break;
            default:
                fillColor = Color.parseColor("#ffffff");
        }

        holder.tvWorld.setTextColor(textColor);
        holder.tvPronoun.setTextColor(textColor);
        holder.tvType.setTextColor(textColor);

        float[] outerR = {topLeft, topLeft, topRight, topRight,bottomRight, bottomRight, bottomLeft, bottomLeft};
       // RoundRectShape shape = new RoundRectShape(outerR, null, null);
       // leafBackground.setShape(shape);
        //leafBackground.getPaint().setShadowLayer(radius, 5, 5, 0x000000);
        //holder.panelWord.setBackgroundDrawable(leafBackground);


        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        //gd.setCornerRadius(roundRadius);
        gd.setCornerRadii(outerR);
        gd.setStroke(strokeWidth, strokeColor);

        holder.panelWord.setBackgroundDrawable(gd);
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvWord)
        public TextView tvWorld;

        @Bind(R.id.tvPronoun)
        public TextView tvPronoun;

        @Bind(R.id.tvType)
        public TextView tvType;

        @Bind(R.id.panelWord)
        public LinearLayout panelWord;

        public ViewHolder(final View itemView) {
            super(itemView);

            try {
                ButterKnife.bind(this, itemView);

            }catch (Exception ex){
                ex.printStackTrace();
            }

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}
