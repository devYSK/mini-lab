package com.yscorp.simple.restdocs

//fun ResultActions.andDocument(
//	identifier: String,
//	vararg snippets: Snippet
//): ResultActions {
//	return andDo(document(identifier, *snippets))
//}

//fun restDocMockMvcBuild(
//	context: WebApplicationContext,
//	provider: RestDocumentationContextProvider
//): MockMvc {
//	return MockMvcBuilders
//		.webAppContextSetup(context)
//		.apply<DefaultMockMvcBuilder>(
//			MockMvcRestDocumentation.documentationConfiguration(provider)
//				.operationPreprocessors()
//				.withRequestDefaults(Preprocessors.prettyPrint())
//				.withResponseDefaults(Preprocessors.prettyPrint())
//		)
//		.apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity(MockSecurityFilter()))
//		.addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
//		.alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
//		.build()
//}
