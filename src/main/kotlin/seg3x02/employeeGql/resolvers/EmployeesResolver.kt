package seg3x02.employeeGql.resolvers

import org.springframework.stereotype.Controller

@Controller
class EmployeesResolver (private val employeeRepository: EmployeeRepository,
                      private val mongoOperations: MongoOperations){
    @QueryMapping
    fun employees(): List<Employee> {
        return employeeRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument employeeId: String): Employee? {
        val employee = employeeRepository.findById(employeeId)
        return employee.orElse(null)
    }

    @QueryMapping
    fun employeeByName(@Argument employeeNumber: Number): Employee? {
        val query = Query()
        query.addCriteria(Criteria.where("name").`is`(employeeNumber))
        val result = mongoOperations.find(query, Employee::class.java)
        return result.firstOrNull()
    }

    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee {
        if (input.name != null &&
                input.dateOfBirth != null &&
                input.city != null &&
                input.salary != null &&
                input.gender != null &&
                input.email != null) {
            val employee = Employee(input.name, input.dateOfBirth, input.city, input.salary, input.gender, input.email)
            employee.employeeId = UUID.randomUUID().toString()
            employeeRepository.save(employee)
            return employee
        } else {
            throw Exception("Invalid input")
        }
    }


    @MutationMapping
    fun deleteEmployee(@Argument("employeeId") id: String) : Boolean {
        employeeRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(@Argument employeeId: String, @Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee {
        val employee = employeeRepository.findById(employeeId)
        employee.ifPresent {
            if (input.name != null) {
                it.name = input.name
            }
            if (input.dateOfBirth != null) {
                it.dateOfBirth = input.dateOfBirth
            }
            if (input.city != null) {
                it.city = input.city
            }
            if (input.salary != null) {
                it.salary = input.salary
            }
            if (input.gender != null) {
                it.gender = input.gender
            }
            if (input.email != null) {
                it.email = input.email
            }
            employeeRepository.save(it)
        }
        return employee.get()
    }
}
