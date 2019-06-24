package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewInputBuilderTest {

    private static final String TAG = "ci";

    @Test
    void shouldBuildReviewInput() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review, TAG);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.tag).isEqualTo(TAG);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test1");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

    @Test
    void shouldNotSetEmptyTag() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review, "");

        assertThat(reviewInput.tag).isNull();
    }

    @Test
    void shouldNotSetNullTag() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder().toReviewInput(review, null);

        assertThat(reviewInput.tag).isNull();
    }

}