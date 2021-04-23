package faas

import faas._
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemResult
import scala.collection.JavaConverters._

object DB {
  val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
    .standard()
    .withRegion(Regions.EU_CENTRAL_1)
    .build();

  def readImage(id: String): MarsImage = {
    val attributeValues = client
      .getItem(
        "mars_images", {
          scala.collection.JavaConverters
            .mapAsJavaMapConverter(Map("id" -> { new AttributeValue(id) }))
            .asJava
        }
      )
      .getItem()
      .asScala
      .toMap
    val prev =
      if (attributeValues.get("prev").isEmpty)
        None
      else
        Some(attributeValues.get("prev").get.getS())
    val next =
      if (attributeValues.get("next").isEmpty)
        None
      else
        Some(attributeValues.get("next").get.getS())

    MarsImage(
      id = attributeValues.get("id").get.getS(),
      title = attributeValues.get("title").get.getS(),
      publish_date = attributeValues.get("publish_date").get.getS(),
      url = attributeValues.get("url").get.getS(),
      prev = prev,
      next = next
    )
  }

  def putUser(id: Int, currentImage: String): PutItemResult = {
    client
      .putItem(
        "mars_users", {
          scala.collection.JavaConverters
            .mapAsJavaMapConverter(
              Map(
                "id" -> { new AttributeValue(id.toString) },
                "current_image" -> { new AttributeValue(currentImage) },
                "subscribed" -> { new AttributeValue().withBOOL(false) }
              )
            )
            .asJava
        }
      )
  }
}
