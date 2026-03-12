package org.hartford.eventguard.service;

import org.hartford.eventguard.dto.PolicyRequest;
import org.hartford.eventguard.dto.PolicyResponse;
import org.hartford.eventguard.entity.EventDomain;
import org.hartford.eventguard.entity.Policy;
import org.hartford.eventguard.exception.ResourceNotFoundException;
import org.hartford.eventguard.repo.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public Policy createPolicy(PolicyRequest request) {
        Policy policy = new Policy();
        policy.setPolicyName(request.getPolicyName());
        policy.setDescription(request.getDescription());
        policy.setDomain(request.getDomain());
        policy.setBaseRate(request.getBaseRate());
        policy.setMaxCoverageAmount(request.getMaxCoverageAmount());
        policy.setIsActive(true);
        return policyRepository.save(policy);
    }

    public PolicyResponse createPolicyDTO(PolicyRequest request) {
        Policy policy = new Policy();
        policy.setPolicyName(request.getPolicyName());
        policy.setDescription(request.getDescription());
        policy.setDomain(request.getDomain());
        policy.setBaseRate(request.getBaseRate());
        policy.setMaxCoverageAmount(request.getMaxCoverageAmount());
        policy.setIsActive(true);
        Policy savedPolicy = policyRepository.save(policy);
        return convertToDTO(savedPolicy);
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public List<Policy> getActivePolicies() {
        return policyRepository.findByIsActiveTrue();
    }

    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
    }

    public Policy updatePolicy(Long id, PolicyRequest request) {
        Policy policy = getPolicyById(id);
        policy.setPolicyName(request.getPolicyName());
        policy.setDescription(request.getDescription());
        policy.setDomain(request.getDomain());
        policy.setBaseRate(request.getBaseRate());
        policy.setMaxCoverageAmount(request.getMaxCoverageAmount());
        return policyRepository.save(policy);
    }

    public void deactivatePolicy(Long id) {
        Policy policy = getPolicyById(id);
        policy.setIsActive(false);
        policyRepository.save(policy);
    }

    public void activatePolicy(Long id) {
        Policy policy = getPolicyById(id);
        policy.setIsActive(true);
        policyRepository.save(policy);
    }

    public List<PolicyResponse> getAllPoliciesDTO() {
        List<Policy> policies = policyRepository.findAll();
        return policies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PolicyResponse> getActivePoliciesDTO() {
        List<Policy> policies = policyRepository.findByIsActiveTrue();
        return policies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PolicyResponse> getPoliciesByDomain(EventDomain domain) {
        List<Policy> policies = policyRepository.findByDomainAndIsActiveTrue(domain);
        return policies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PolicyResponse getPolicyByIdDTO(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        return convertToDTO(policy);
    }

    private PolicyResponse convertToDTO(Policy policy) {
        PolicyResponse dto = new PolicyResponse();
        dto.setPolicyId(policy.getPolicyId());
        dto.setPolicyName(policy.getPolicyName());
        dto.setDescription(policy.getDescription());
        dto.setDomain(policy.getDomain());
        dto.setBaseRate(policy.getBaseRate());
        dto.setMaxCoverageAmount(policy.getMaxCoverageAmount());
        dto.setIsActive(policy.getIsActive());
        return dto;
    }
}
