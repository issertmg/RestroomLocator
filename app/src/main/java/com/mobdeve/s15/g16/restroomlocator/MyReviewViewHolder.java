package com.mobdeve.s15.g16.restroomlocator;

        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.recyclerview.widget.RecyclerView;

        import com.mobdeve.s15.g16.restroomlocator.models.Review;
        import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;

public class MyReviewViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivReviewImageOne, ivReviewImageTwo, ivReviewImageThree;
    private TextView tvReviewUsername, tvReviewNumReviews;

    public MyReviewViewHolder(View itemView) {
        super(itemView);
        this.tvReviewUsername = itemView.findViewById(R.id.tvReviewUsername);
        this.tvReviewNumReviews = itemView.findViewById(R.id.tvReviewNumReviews); //FIXME:
        this.ivReviewImageOne = itemView.findViewById(R.id.ivReviewImageOne);
        this.ivReviewImageTwo = itemView.findViewById(R.id.ivReviewImageTwo);
        this.ivReviewImageThree = itemView.findViewById(R.id.ivReviewImageThree);
    }

    public void bindData(Review r, String username) {

        MyFirestoreHelper.downloadImageIntoImageView(r.getId(),
                r.getImageUri1(), r.getImageUri2(), r.getImageUri3(),
                this.ivReviewImageOne, this.ivReviewImageTwo, this.ivReviewImageThree);

        this.tvReviewUsername.setText(username);
    }
}

