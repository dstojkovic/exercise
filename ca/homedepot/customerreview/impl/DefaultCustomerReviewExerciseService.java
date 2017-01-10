package ca.homedepot.customerreview.impl;

import java.util.List;

import ca.homedepot.customerreview.CustomerReviewExerciseService;
import ca.homedepot.customerreview.dao.CurseDao;
import ca.homedepot.customerreview.exceptions.CreateCustomerReviewException;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.constants.CustomerReviewConstants;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import org.apache.log4j.Logger;

public class DefaultCustomerReviewExerciseService implements CustomerReviewExerciseService {
	
	private static final Logger LOG = Logger.getLogger(DefaultCustomerReviewExerciseService.class.getName());
	
	private CustomerReviewService customerReviewService;
	private CurseDao curseDao;

	@Required
	protected CustomerReviewService getCustomerReviewService() {
		return customerReviewService;
	}

	public void setCustomerReviewService(CustomerReviewService customerReviewService) {
		this.customerReviewService = customerReviewService;
	}

	protected CurseDao getCurseDao() {
		return curseDao;
	}
	
	@Required
	public void setCurseDao(CurseDao curseDao) {
		this.curseDao = curseDao;
	}

	@Override
	public Integer getNumberOfReviewsWithinGivenRange(ProductModel paramProductModel, Double minRange, Double maxRange) 
	{
		int totalReviews = 0;
		
		List<CustomerReviewModel> productReviewList = getCustomerReviewService().getReviewsForProduct(paramProductModel);
		
		if (productReviewList != null && !productReviewList.isEmpty()) 
		{
			for (CustomerReviewModel productReview : productReviewList) 
			{
				if (productReview != null) 
				{
					// I assume CustomerReviewModel has getRating method
					if (isRatingWithinRange(productReview.getRating(), minRange, maxRange))
						totalReviews++;
				}
			}
		}
		
		return totalReviews;
	}
	
	@Override
	public CustomerReviewModel validateAndCreateCustomerReview(Double rating, String headline, String comment, UserModel user, ProductModel product) throws CreateCustomerReviewException
	{
		checkCommentForCurseWords(comment) ;
		
		verifyRatingValue(rating);
		
		return getCustomerReviewService().createCustomerReview(rating, headline, comment, user, product);
	}
	
	@Override
	public CustomerReviewModel createCustomerReview(Double paramDouble, String paramString1, String paramString2, UserModel paramUserModel, ProductModel paramProductModel)
	{
		return getCustomerReviewService().createCustomerReview(paramDouble, paramString1, paramString2, paramUserModel, paramProductModel);
	}

	@Override
	public void updateCustomerReview(CustomerReviewModel paramCustomerReviewModel, UserModel paramUserModel, ProductModel paramProductModel) 
	{
		getCustomerReviewService().updateCustomerReview(paramCustomerReviewModel, paramUserModel, paramProductModel);
	}

	@Override
	public List<CustomerReviewModel> getAllReviews(ProductModel paramProductModel) 
	{
		return getCustomerReviewService().getAllReviews(paramProductModel);
	}

	@Override
	public Double getAverageRating(ProductModel paramProductModel) 
	{
		return getCustomerReviewService().getAverageRating(paramProductModel);
	}

	@Override
	public Integer getNumberOfReviews(ProductModel paramProductModel)
	{
		return getCustomerReviewService().getNumberOfReviews(paramProductModel);
	}

	@Override
	public List<CustomerReviewModel> getReviewsForProduct(ProductModel paramProductModel)
	{
		return getCustomerReviewService().getReviewsForProduct(paramProductModel);
	}

	@Override
	public List<CustomerReviewModel> getReviewsForProductAndLanguage(ProductModel paramProductModel, LanguageModel paramLanguageModel) 
	{
		return getCustomerReviewService().getReviewsForProductAndLanguage(paramProductModel, paramLanguageModel);
	}

	private void checkCommentForCurseWords(String comment) throws CreateCustomerReviewException
	{
		if (comment != null && comment.length() > 0)	//instead of manually checking if a string is empty some String util method like from Apache Commons: StringUtils.isEmpty should be used to verify if a string is not empty
		{												
			List<String> curseList = getCurseDao().getListOfCurseWords();
			if (curseList != null && !curseList.isEmpty())
			{
				String commentLowerCase = comment.toLowerCase();
				for (String curseWord : curseList) 
				{
					if (commentLowerCase.contains(curseWord.toLowerCase()))
						throw new CreateCustomerReviewException("Customer Review contains curse word!");
				}
			} else {
				LOG.error("There is issue fetching list of curse words");
				throw new RuntimeException("issue fetching list of curse words");
			}
		}
	}

	private void verifyRatingValue(Double rating) throws CreateCustomerReviewException
	{
		if (rating != null && rating.compareTo(CustomerReviewConstants.DEFAULTS.MINIMAL_RATING) < 0)
			throw new CreateCustomerReviewException("Rating less than minimal rating value!");
	}

	private boolean isRatingWithinRange(Double rating, Double minRange, Double maxRange)
	{
		return rating != null && rating.compareTo(minRange) >= 0 && rating.compareTo(maxRange) <= 0; 
	}

}
