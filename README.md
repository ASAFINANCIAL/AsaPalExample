# AsaPalExample

Example app shows how to use ASA Open API and deep links interaction.

# SplashActivity

Used to receive information from deep link and save it to local storage using Paper library. The parameters which should be in deep link for API to work properly: AsaConsumerCode, AsaFintechCode, FintechName.

# MainActivity

Contains ViewPager with two fragments: UserInfoFragment and TransactionsFragment. To get data from BE uses endpoints which can be found in ApiInterface file. Parameters which is used in the requests are the same: 
AsaConsumerCode, AsaFintechCode, FintechName which you get from deep link

And the another set of parameters, you will need to get from ASA, the file BEConstants is not included in the repo: 
hashMap["Ocp-Apim-Subscription-Key"] = BEConstants.API_KEY
hashMap["ApplicationCode"] = BEConstants.APP_CODE
hashMap["AuthorizationKey"] = BEConstants.AUTHORIZATION_KEY

# RetrofitClientInterface

Contains standart request interceptor and the link to api: "https://openapi.asacore.com/".

# ApiInterface

We have two endpoints in Open API:
@GET("Balance/Accounts") - to get information about accounts.
@POST("Transactions") - to get information about transactions, please note that you need to pass empty body into request.

# Models package

Representing evey data time which is returned from API.

# UserInfoFragment and TransactionFragment

Used to represent data.

# Application class

Only need to init Paperdb component.
