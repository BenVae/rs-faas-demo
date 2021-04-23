package faas

import scala.util.Random

object RoverHealthHandler {

  def handleRequest(): Int = {
    Thread.sleep(Random.between(1_500, 4_500))
    return Random.between(50, 100)
  }
}
