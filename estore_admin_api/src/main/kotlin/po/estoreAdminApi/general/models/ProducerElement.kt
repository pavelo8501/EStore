package po.estoreAdminApi.general.models

import po.estoreAdminApi.database.services.ProducerEntity

class ProducerElement() : ElementClass()  {

    private val producerEntity :  ProducerEntity?
         get() = super.dbEntity as ProducerEntity?


}