package po.estoreAdminApi

import io.mockk.*
import adminApi.com.datareader.classes.ProviderDataListener
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.data.DataProvider
import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProviderManagerTest {

    private lateinit var providerManager: ProviderManager
    private lateinit var mockProviderDataListener: ProviderDataListener

    @BeforeEach
    fun setUp() {
        mockProviderDataListener = mockk(relaxed = true)
        providerManager = spyk(ProviderManager(), recordPrivateCalls = true)

        val dataListenerField = ProviderManager::class.java.getDeclaredField("dataListener")
        dataListenerField.isAccessible = true
        dataListenerField.set(providerManager, mockProviderDataListener)
    }

    @Test
    fun `test initialization`() {
        verify { mockProviderDataListener.onStart() }
    }

    @Test
    fun `test createDataProvider`() {
        val provider = providerManager.createDataProvider<ActionConnector>(1, "action")
        assertNotNull(provider)
        assertEquals(1, provider.supplierId)
        assertEquals("action", provider.name)
    }

    @Test
    fun `test getDataProvider`() {
        providerManager.createDataProvider<ActionConnector>(1, "action")
        val provider = providerManager.getDataProvider("action")
        assertNotNull(provider)
        assertEquals(1, provider.supplierId)
    }

    @Test
    fun `test getProvider`() {
        providerManager.createDataProvider<ActionConnector>(1, "action")
        val provider = providerManager.getProvider(1)
        assertNotNull(provider)
        assertEquals("action", provider.name)
    }

    @Test
    fun `test sendProducers`() {
        val items = listOf(ProducerData(1,1,"Some","Some Producer"))
        ProviderManager.sendProducers(1, items)
        assertEquals(1, ProviderManager.producersSubject.value?.supplierId)
        assertEquals("producers", ProviderManager.producersSubject.value?.dataType)
        assertEquals(items, ProviderManager.producersSubject.value?.items)
    }

    @Test
    fun `test sendCategories`() {
        val items = listOf(CategoryData(1,1,"2",1,"Some Category"))
        ProviderManager.sendCategories(1, items)
        assertEquals(1, ProviderManager.categoriesSubject.value?.supplierId)
        assertEquals("categories", ProviderManager.categoriesSubject.value?.dataType)
        assertEquals(items, ProviderManager.categoriesSubject.value?.items)
    }

    @Test
    fun `test sendProducts`() {
        val items = listOf(
           ProductData(
               1,
               1,
               "ProviderId",
               "ProducrId",
               "Produer Code",
               "1,2,3",
                "Product Name",
               1,
               10.2,
               "EUR",
               "ean code",
               10,
               10,
               10,
               20,
               "additional parameters"
           )
        )
        ProviderManager.sendProducts(1, items)
        assertEquals(1, ProviderManager.productsSubject.value?.supplierId)
        assertEquals("products", ProviderManager.productsSubject.value?.dataType)
        assertEquals(items, ProviderManager.productsSubject.value?.items)
    }

    @Test
    fun `test getCategories`() {
        val mockProvider = mockk<DataProvider>(relaxed = true)
        every { providerManager.getDataProvider("action") } returns mockProvider
        providerManager.getCategories("action")
        verify { mockProvider.getCategories() }
    }

    @Test
    fun `test getProducers`() {
        val mockProvider = mockk<DataProvider>(relaxed = true)
        every { providerManager.getDataProvider("action") } returns mockProvider
        providerManager.getProducers("action")
        verify { mockProvider.getProducers() }
    }

    @Test
    fun `test getProducts`() {
        val mockProvider = mockk<DataProvider>(relaxed = true)
        every { providerManager.getDataProvider("action") } returns mockProvider
        providerManager.getProducts("action")
        verify { mockProvider.getProducts() }
    }



}