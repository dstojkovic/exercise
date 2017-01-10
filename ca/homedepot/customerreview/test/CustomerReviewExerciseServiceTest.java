package ca.homedepot.customerreview.test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import ca.homedepot.customerreview.exceptions.CreateCustomerReviewException;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

public class CustomerReviewExerciseServiceTest extends ServicelayerTransactionalTest {

	@Resource
	private CustomerReviewExerciseService customerReviewService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	
	private UserModel userModel01;
	private ProductModel productModel01;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		this.productModel01 = this.productService.getProduct("testProduct1");
		this.userModel01 = this.userService.getUser("anonymous");

	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void testGetNumberOfReviewsWithinGivenRange()
	{
		Assert.assertEquals("no rating", 0.0D, this.customerReviewService.getNumberOfReviews(this.productModel01).doubleValue(), 0.001D);
		this.customerReviewService.createCustomerReview(Double.valueOf(4.5D), "headline_anonymous", "comment_anonymous", this.userModel01, 
				this.productModel01);
		this.customerReviewService.createCustomerReview(Double.valueOf(3.5D), "headline_anonymous", "comment_anonymous", this.userModel01, 
				this.productModel01);
		
		Assert.assertEquals("one review with rating between 4.5 and 5.0", 1.0D, this.customerReviewService.getNumberOfReviewsWithinGivenRange(this.productModel01, 4.5D, 5.0D).doubleValue(), 0.001D);
		
	}

	@Test(expected=CreateCustomerReviewException.class)
	public void testValidateAndCreateCustomerReview_RatingLessThan0_ShouldThrowException()
	{
		this.customerReviewService.validateAndCreateCustomerReview(Double.valueOf(-1.0D), "headline_anonymous", "comment_anonymous", this.userModel01, 
				this.productModel01);
	}

	@Test(expected=CreateCustomerReviewException.class)
	public void testValidateAndCreateCustomerReview_CommentContainsCurse_ShouldThrowException()
	{
		this.customerReviewService.validateAndCreateCustomerReview(Double.valueOf(1.0D), "headline_anonymous", "comment with curse word", this.userModel01, 
				this.productModel01);
	}

	@Test
	public void testValidateAndCreateCustomerReview()
	{
		Assert.assertEquals("no rating", 0.0D, this.customerReviewService.getNumberOfReviews(this.productModel01).doubleValue(), 0.001D);
		this.customerReviewService.validateAndCreateCustomerReview(Double.valueOf(2.0D), "headline_anonymous", "comment_exercise", this.userModel01, 
				this.productModel01);
		Assert.assertEquals("1 rating", 1.0D, this.customerReviewService.getNumberOfReviews(this.productModel01).doubleValue(), 0.001D);
	}
}
