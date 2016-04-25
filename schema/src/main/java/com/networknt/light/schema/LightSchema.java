package com.networknt.light.schema;

import graphql.schema.*;

import javax.persistence.criteria.Root;

import static graphql.Scalars.GraphQLString;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLBoolean;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
/**
 * Created by steve on 4/25/2016.
 */
public class LightSchema {

    public static GraphQLObjectType userType = newObject()
            .name("User")
            .description("A logged in user of the framework.")
            .field(newFieldDefinition()
                    .name("userId")
                    .description("The unique userId picked during sign up.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("email")
                    .description("The email address that can be used to log in.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("firstName")
                    .description("User's first name or given name.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("lastName")
                    .description("User's last name or family name.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("accessToken")
                    .description("A JWT access token.")
                    .type(GraphQLBoolean)
                    .build())
            .field(newFieldDefinition()
                    .name("refreshToken")
                    .description("A refresh token.")
                    .type(GraphQLBoolean)
                    .build())
            .build();

    public static GraphQLInputObjectType inputUserSignInType = newInputObject()
            .name("inputUserSginInObjectType")
            .field(newInputObjectField()
                    .name("userIdEmail")
                    .description("Ether userId or email can be used for signIn.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("password")
                    .description("Password picked during signUp.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("rememberMe")
                    .description("Private computer and long lived token will be returned.")
                    .type(GraphQLBoolean)
                    .build())
            .build();

    public static GraphQLInputObjectType inputUserSignUpType = newInputObject()
            .name("inputUserSginInObjectType")
            .field(newInputObjectField()
                    .name("userId")
                    .description("Unique userId picked during signUp.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("email")
                    .description("Email address.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("password")
                    .description("Password picked during signUp.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("passwordConfirm")
                    .description("Password confirmation.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("firstName")
                    .description("First name or given name.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("lastName")
                    .description("Last name or family name.")
                    .type(GraphQLString)
                    .build())
            .build();

    public static GraphQLObjectType queryType = newObject()
            .name("QueryType")
            .field(newFieldDefinition()
                    .name("hero")
                    .type(characterInterface)
                    .argument(newArgument()
                            .name("episode")
                            .description("If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode.")
                            .type(episodeEnum)
                            .build())
                    .dataFetcher(new StaticDataFetcher(StarWarsData.getArtoo()))
                    .build())
            .field(newFieldDefinition()
                    .name("human")
                    .type(humanType)
                    .argument(newArgument()
                            .name("id")
                            .description("id of the human")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .dataFetcher(StarWarsData.getHumanDataFetcher())
                    .build())
            .field(newFieldDefinition()
                    .name("droid")
                    .type(droidType)
                    .argument(newArgument()
                            .name("id")
                            .description("id of the droid")
                            .type(new GraphQLNonNull(GraphQLString))
                            .build())
                    .dataFetcher(StarWarsData.getDroidDataFetcher())
                    .build())
            .build();

    public static GraphQLObjectType mutationType = GraphQLObjectType.newObject()
            .name("mutationType")
            .description("All supported mutations.")
            .field(newFieldDefinition()
                    .name("signUp")
                    .type(userType)
                    .argument(newArgument()
                            .name("Sign up user.")
                            .type(inputUserSignUpType)
                            .build())
                    .dataFetcher(new DataFetcher() {
                            @Override
                            public Object get(DataFetchingEnvironment environment) {
                                    int newNumber = environment.getArgument("newNumber");
                                    Root root = (Root) environment.getSource();
                                    return root.changeNumber(newNumber);
                            }
                    })
                    .build())
            .field(newFieldDefinition()
                    .name("signIn")
                    .type(userType)
                    .argument(newArgument()
                            .name("Sign in user.")
                            .type(inputUserSignInType)
                            .build())
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment environment) {
                            int newNumber = environment.getArgument("newNumber");
                            Root root = (Root) environment.getSource();
                            return root.failToChangeTheNumber(newNumber);
                        }
                    })
                    .build())
            .build();


    public static GraphQLSchema lightSchema = GraphQLSchema.newSchema()
            .query(queryType)
            .mutation(mutationType)
            .build();
}
