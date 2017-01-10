package ca.homedepot.customerreview;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import de.hybris.platform.customerreview.CustomerReviewService;
import ca.homedepot.customerreview.exceptions.CreateCustomerReviewException;

public interface CustomerReviewExerciseService extends CustomerReviewService {
	
	public Integer getNumberOfReviewsWithinGivenRange(ProductModel paramProductModel, Double minRange, Double maxRange);
	public CustomerReviewModel validateAndCreateCustomerReview(Double rating, String headline, String comment, UserModel user, ProductModel product) throws CreateCustomerReviewException;
}
