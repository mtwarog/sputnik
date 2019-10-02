package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.changes.ReviewInput;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.ReviewBuilder;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;

@ExtendWith(MockitoExtension.class)
class ReviewInputBuilderTest {

    @Mock
    private CommentFilter commentFilter;

    @Test
    void shouldBuildReviewInput() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        ReviewInput reviewInput = new ReviewInputBuilder(commentFilter).toReviewInput(review);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test1");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

    @Test
    public void shouldBuildReviewInputWithFilter() {
        Configuration config = ConfigurationBuilder.initFromResource("test.properties");
        Review review = ReviewBuilder.buildReview(config);

        when(commentFilter.filter(anyString(), anyInt())).thenReturn(false);
        ReviewInput reviewInput = new ReviewInputBuilder(commentFilter).toReviewInput(review);

        assertThat(reviewInput.message).isEqualTo("Total 8 violations found");
        assertThat(reviewInput.comments).hasSize(4);
        assertThat(reviewInput.comments.get("filename1")).hasSize(2);
        assertThat(reviewInput.comments.get("filename1").iterator().next().line).isEqualTo(0);
        assertThat(reviewInput.comments.get("filename1").get(0).message).isEqualTo("test1");
        assertThat(reviewInput.labels.get("Code-Review")).isEqualTo((short) 1);
    }

}