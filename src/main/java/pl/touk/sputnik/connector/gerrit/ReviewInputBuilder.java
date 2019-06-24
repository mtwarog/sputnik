package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Joiner;
import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReviewInputBuilder {

    @NotNull
    public ReviewInput toReviewInput(@NotNull Review review, @Nullable String tag) {
        ReviewInput reviewInput = new ReviewInput();
        reviewInput.message = Joiner.on(". ").join(review.getMessages());
        reviewInput.labels = new HashMap<>(review.getScores());
        reviewInput.tag = tag;
        reviewInput.comments = new HashMap<>();
        for (ReviewFile file : review.getFiles()) {
            List<ReviewInput.CommentInput> comments = new ArrayList<>();
            for (Comment comment : file.getComments()) {
                ReviewInput.CommentInput commentInput = new ReviewInput.CommentInput();
                commentInput.line = comment.getLine();
                commentInput.message = comment.getMessage();
                comments.add(commentInput);
            }
            reviewInput.comments.put(file.getReviewFilename(), comments);
        }

        return reviewInput;
    }
}
