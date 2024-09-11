package adminApi.com.general.models

import adminApi.com.database.services.ProducerEntity

class ProducerElement() : ElementClass()  {

    private val producerEntity :  ProducerEntity?
         get() = super.dbEntity as ProducerEntity?


}