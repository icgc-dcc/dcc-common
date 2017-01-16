/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.common.core.model;

import static org.icgc.dcc.common.core.model.FieldNames.SubmissionFieldNames.SUBMISSION_DONOR_ID;
import static org.icgc.dcc.common.core.model.FieldNames.SubmissionFieldNames.SUBMISSION_MATCHED_SAMPLE_ID;
import static org.icgc.dcc.common.core.model.FieldNames.SubmissionFieldNames.SUBMISSION_SPECIMEN_ID;

import org.icgc.dcc.common.core.model.FeatureTypes.FeatureType;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class FieldNames {

  /**
   * Internal mongo ID field name.
   */
  public static final String MONGO_INTERNAL_ID = "_id";

  /**
   * Field names in the original submission format.
   */
  public static class SubmissionFieldNames {

    public static final String SUBMISSION_DONOR_ID = "donor_id";
    public static final String SUBMISSION_SPECIMEN_ID = "specimen_id";
    public static final String SUBMISSION_ANALYZED_SAMPLE_ID = "analyzed_sample_id";
    public static final String SUBMISSION_MATCHED_SAMPLE_ID = "matched_sample_id";
    public static final String SUBMISSION_GENE_AFFECTED = "gene_affected";
    public static final String SUBMISSION_TRANSCRIPT_AFFECTED = "transcript_affected";
    public static final String SUBMISSION_DIGITAL_IMAGE_OF_STAINED_SECTION = "digital_image_of_stained_section";
    public static final String SUBMISSION_MUTATION = "mutation";

    public static final String SUBMISSION_OBSERVATION_ANALYSIS_ID = "analysis_id";
    public static final String SUBMISSION_OBSERVATION_ANALYZED_SAMPLE_ID = SUBMISSION_ANALYZED_SAMPLE_ID;
    public static final String SUBMISSION_OBSERVATION_MATCHED_SAMPLE_ID = SUBMISSION_MATCHED_SAMPLE_ID;

    public static final String SUBMISSION_OBSERVATION_ASSEMBLY_VERSION = "assembly_version";

    public static final String SUBMISSION_OBSERVATION_CHROMOSOME = "chromosome";
    public static final String SUBMISSION_OBSERVATION_CHROMOSOME_START = "chromosome_start";
    public static final String SUBMISSION_OBSERVATION_CHROMOSOME_END = "chromosome_end";
    public static final String SUBMISSION_OBSERVATION_MUTATION_TYPE = "mutation_type";
    public static final String SUBMISSION_OBSERVATION_VARIANT_TYPE = "variant_type";

    public static final String SUBMISSION_OBSERVATION_CONTROL_GENOTYPE = "control_genotype";
    public static final String SUBMISSION_OBSERVATION_TUMOUR_GENOTYPE = "tumour_genotype";
    public static final String SUBMISSION_OBSERVATION_REFERENCE_GENOME_ALLELE = "reference_genome_allele";
    public static final String SUBMISSION_OBSERVATION_VARIANT_ALLELE = "variant_allele";

    public static final String SUBMISSION_OBSERVATION_MUTATED_FROM_ALLELE = "mutated_from_allele";
    public static final String SUBMISSION_OBSERVATION_MUTATED_TO_ALLELE = "mutated_to_allele";

    public static final String SUBMISSION_OBSERVATION_CHROMOSOME_STRAND = "chromosome_strand";
    public static final String SUBMISSION_OBSERVATION_SEQUENCING_STRATEGY = "sequencing_strategy";
    public static final String SUBMISSION_OBSERVATION_RAW_DATA_REPOSITORY = "raw_data_repository";
    public static final String SUBMISSION_OBSERVATION_RAW_DATA_ACCESSION = "raw_data_accession";

  }

  /**
   * Field names used in the normalizer component.
   */
  @UtilityClass
  public static class NormalizerFieldNames {

    /**
     * Field to act as primary key between primary and secondary files.
     */
    public static final String NORMALIZER_OBSERVATION_ID = "observation_id";
    public static final String NORMALIZER_MARKING = "marking";

    /**
     * Formerly in the submission files.
     */
    public static final String NORMALIZER_MUTATION = "mutation";

  }

  /**
   * Field names used in the annotator component.
   */
  @UtilityClass
  public static class AnnotatorFieldNames {

    // Represent some of the SSM_S and SGV_S headers
    public static final String ANNOTATOR_AMINO_ACID_CHANGE = "aa_change";
    public static final String ANNOTATOR_CDS_CHANGE = "cds_change";
    public static final String ANNOTATOR_CDS_MUTATION = "cds_mutation";
    public static final String ANNOTATOR_PROTEIN_DOMAIN_AFFECTED = "protein_domain_affected";
    public static final String ANNOTATOR_GENE_BUILD_VERSION = "gene_build_version";
    public static final String ANNOTATOR_NOTE = "note";

  }

  /**
   * Field names used in the identifier component.
   */
  @UtilityClass
  public static class IdentifierFieldNames {

    public static final String SURROGATE_DONOR_ID = String.format("_%s", SUBMISSION_DONOR_ID);
    public static final String SURROGATE_SPECIMEN_ID = String.format("_%s", SUBMISSION_SPECIMEN_ID);
    public static final String SURROGATE_SAMPLE_ID = "_sample_id";
    public static final String SURROGATE_MUTATION_ID = "_mutation_id";

  }

  /**
   * Names for the fields in the loader.
   */
  @UtilityClass
  public static class LoaderFieldNames {

    public static final String OBSERVATION_TYPE = "_type";
    public static final String PROJECT_ID = "_project_id";
    public static final String GENE_ID = "_gene_id";
    public static final String TRANSCRIPT_ID = "_transcript_id";

    public static final String CONSEQUENCE_ARRAY_NAME = "consequence";
    public static final String OBSERVATION_ARRAY_NAME = "observation";

    public static final String AVAILABLE_RAW_SEQUENCE_DATA = "available_raw_sequence_data";
    public static final String STUDY = "study";
    public static final String SUMMARY = "_summary";

    public static final String SURROGATE_MATCHED_SAMPLE_ID = String.format("_%s", SUBMISSION_MATCHED_SAMPLE_ID);

  }

  /**
   * Names for the fields in the importer.
   */
  @UtilityClass
  public static class ImporterFieldNames {

    public static final String CGC_ID = "CGC";
    public static final String GO_ID = "GO";

  }

  /**
   * Names for the fields in the reporter.
   */
  @UtilityClass
  public static class ReporterFieldNames {

    public static final String RELEASE_NAME = "release_name";

  }

  /**
   * Project field names.
   */
  public static final String PROJECT_ID = "_project_id"; // "BRCA-UK" for instance
  public static final String PROJECT_ICGC_ID = "icgc_id"; // "1133" for instance
  public static final String PROJECT_FORMER_SUBMISSION_ID = "former_submission_id"; // "1133.0" for instance
  public static final String PROJECT_ALIAS = "alias"; // "CLL Genome" for instance
  public static final String PROJECT_COLOUR = "colour";
  public static final String PROJECT_DISPLAY_NAME = "project_name"; // "Bladder Urothelial Cancer - TGCA, US" for
                                                                    // instance
  public static final String PROJECT_TUMOUR_TYPE = "tumour_type"; // "Bladder cancer" for instance
  public static final String PROJECT_TUMOUR_SUBTYPE = "tumour_subtype"; // "Invasive urothelial bladder cancer" for
                                                                        // instance
  public static final String PROJECT_PRIMARY_SITE = "primary_site"; // "Bladder" for instance
  public static final String PROJECT_PRIMARY_COUNTRIES = "primary_countries"; // "United States" for instance
  public static final String PROJECT_PARTNER_COUNTRIES = "partner_countries"; // "Mexico" for instance
  public static final String PROJECT_PUBMED_IDS = "pubmed_ids";
  public static final String PROJECT_SUMMARY = "_summary";
  public static final String PROJECT_SUMMARY_REPOSITORY = "repository";
  public static final String PROJECT_SUMMARY_TOTAL_DONOR_COUNT = "_total_donor_count";
  public static final String PROJECT_SUMMARY_LIVE_DONOR_COUNT = "_total_live_donor_count";
  public static final String PROJECT_SUMMARY_STATE = "_state";

  /**
   * Special fields, usually appearing in all clinical files as well as primary/meta experimental files.
   */
  public static final String URI = "uri";
  public static final String DB_XREF = "db_xref";

  /**
   * Donor field names.
   */
  public static final String DONOR_ID = IdentifierFieldNames.SURROGATE_DONOR_ID;

  public static final String DONOR_SUMMARY = LoaderFieldNames.SUMMARY;
  public static final String DONOR_SUMMARY_REPOSITORY = "repository";
  public static final String DONOR_SUMMARY_EXPERIMENTAL_ANALYSIS = "experimental_analysis_performed";
  public static final String DONOR_SUMMARY_EXPERIMENTAL_ANALYSIS_SAMPLE_COUNTS =
      "experimental_analysis_performed_sample_count";
  public static final String DONOR_SUMMARY_AGE_AT_DIAGNOSIS_GROUP = "_age_at_diagnosis_group";
  public static final String DONOR_SUMMARY_AFFECTED_GENE_COUNT = "_affected_gene_count";
  public static final String DONOR_SUMMARY_STATE = "_state";
  @Deprecated
  public static final String DONOR_SUMMARY_COMPLETE = "_complete";

  public static final String DONOR_GENES = "gene";
  public static final String DONOR_GENE_GENE_ID = LoaderFieldNames.GENE_ID;
  public static final String DONOR_GENE_SUMMARY = "_summary";
  public static final String DONOR_PROJECT_ID = PROJECT_ID;
  public static final String DONOR_PROJECT = "project";
  public static final String DONOR_AGE_AT_DIAGNOSIS = "donor_age_at_diagnosis";
  public static final String DONOR_SPECIMEN = "specimen";
  public static final String DONOR_SPECIMEN_ID = IdentifierFieldNames.SURROGATE_SPECIMEN_ID;
  public static final String DONOR_SAMPLE = "sample"; // TODO: reuse loader's
  public static final String DONOR_SAMPLE_ID = IdentifierFieldNames.SURROGATE_SAMPLE_ID;
  public static final String DONOR_SAMPLE_ANALYZED_SAMPLE_ID = "analyzed_sample_id";
  public static final String DONOR_SAMPLE_SEQUENCE_DATA = LoaderFieldNames.AVAILABLE_RAW_SEQUENCE_DATA;

  public static final String DONOR_OPTIONAL = "optional";

  /**
   * CGHub field names.
   */
  public static final String SEQUENCE_DATA_PROJECT_ID = PROJECT_ID;
  public static final String SEQUENCE_DATA_LEGACY_SAMPLE_ID = "legacy_sample_id";
  public static final String SEQUENCE_DATA_LIBRARY_STRATEGY = "library_strategy";
  public static final String SEQUENCE_DATA_REPOSITORY = "repository";

  /**
   * Gene field names.
   */
  public static final String GENE_ID = LoaderFieldNames.GENE_ID; // TODO: remove?
  public static final String GENE_SYMBOL = "symbol";
  public static final String GENE_DONORS = "donor";
  public static final String GENE_DONOR_DONOR_ID = IdentifierFieldNames.SURROGATE_DONOR_ID;
  public static final String GENE_DONOR_PROJECT = "project";
  public static final String GENE_DONOR_SUMMARY = "_summary";
  public static final String GENE_PROJECTS = "project";
  public static final String GENE_PROJECT_PROJECT_ID = PROJECT_ID;
  public static final String GENE_PROJECT_SUMMARY = "_summary";
  public static final String GENE_CANONICAL_TRANSCRIPT_ID = "canonical_transcript_id";
  public static final String GENE_TRANSCRIPTS = "transcripts";
  public static final String GENE_TRANSCRIPTS_TRANSCRIPT_ID = "id";
  public static final String GENE_TRANSCRIPTS_TRANSCRIPT_EXONS = "exons";
  public static final String GENE_TRANSCRIPTS_TRANSCRIPT_DOMAINS = "domains";
  public static final String GENE_SETS = "sets";
  public static final String GENE_SETS_TYPE = "type";

  public static final String GENE_UNIPROT_IDS = "external_db_ids.uniprotkb_swissprot";

  public static final String GENE_SUMMARY = "_summary";
  public static final String GENE_SUMMARY_AFFECTED_TRANSCRIPT_IDS = "_affected_transcript_id";
  public static final String GENE_SUMMARY_AFFECTED_PROJECT_COUNT = "_affected_project_count";
  public static final String GENE_SUMMARY_AFFECTED_DONOR_COUNT = "_affected_donor_count";
  public static final String GENE_SUMMARY_UNIQUE_MUTATION_COUNT = "_unique_mutation_count";
  public static final String GENE_SUMMARY_TOTAL_MUTATION_COUNT = "_total_mutation_count";

  /**
   * Consequence field names.
   */
  public static final String CONSEQUENCE_AA_MUTATION = "aa_mutation";

  /**
   * Observation field names.
   */
  public static final String OBSERVATION_ID = MONGO_INTERNAL_ID;
  public static final String OBSERVATION_MUTATION_ID = IdentifierFieldNames.SURROGATE_MUTATION_ID;
  public static final String OBSERVATION_TYPE = LoaderFieldNames.OBSERVATION_TYPE;
  public static final String OBSERVATION_DONOR = "donor";
  public static final String OBSERVATION_DONOR_ID = IdentifierFieldNames.SURROGATE_DONOR_ID;
  public static final String OBSERVATION_PROJECT = "observation_project";
  public static final String OBSERVATION_CONSEQUENCE_TYPES = "consequence_type";
  public static final String OBSERVATION_FUNCTIONAL_IMPACT_PREDICTION_SUMMARY = "functional_impact_prediction_summary";
  public static final String OBSERVATION_CONSEQUENCES = "consequence";
  public static final String OBSERVATION_CONSEQUENCES_CONSEQUENCE_TYPE = "consequence_type";
  public static final String OBSERVATION_CONSEQUENCES_CONSEQUENCE_FUNCTIONAL_IMPACT_PREDICTION_SUMMARY =
      "functional_impact_prediction_summary";
  public static final String OBSERVATION_CONSEQUENCES_CONSEQUENCE_FUNCTIONAL_IMPACT_PREDICTION =
      "functional_impact_prediction";
  public static final String OBSERVATION_CONSEQUENCES_AA_MUTATION = CONSEQUENCE_AA_MUTATION;
  public static final String OBSERVATION_CONSEQUENCES_CONSEQUENCE_CANONICAL = "_is_canonical_transcript";
  public static final String OBSERVATION_CONSEQUENCES_TRANSCRIPT_ID = LoaderFieldNames.TRANSCRIPT_ID;
  public static final String OBSERVATION_CONSEQUENCES_GENE_ID = LoaderFieldNames.GENE_ID;
  public static final String OBSERVATION_GENE = "gene";
  public static final String OBSERVATION_PLATFORM = "platform";
  public static final String OBSERVATION_VALIDATION_STATUS = "validation_status";
  public static final String OBSERVATION_VALIDATION_PLATFORM = "validation_platform";
  public static final String OBSERVATION_VERIFICATION_STATUS = "verification_status";
  public static final String OBSERVATION_VERIFICATION_PLATFORM = "verification_platform";
  public static final String OBSERVATION_SEQUENCING_STRATEGY = "sequencing_strategy";
  public static final String OBSERVATION_IS_ANNOTATED = "is_annotated";

  public static String getPartitionTypeFieldName(String type) {
    return type;
  }

  /**
   * Mutation field names.
   */
  public static final String MUTATION_ID = IdentifierFieldNames.SURROGATE_MUTATION_ID;
  public static final String MUTATION_CHROMOSOME = "chromosome";
  public static final String MUTATION_CHROMOSOME_START = "chromosome_start";
  public static final String MUTATION_CHROMOSOME_END = "chromosome_end";
  public static final String MUTATION_OBSERVATIONS = "observation";
  public static final String MUTATION_OCCURRENCES = "ssm_occurrence";
  public static final String MUTATION_OBSERVATION_DONOR = "donor";
  public static final String MUTATION_OBSERVATION_PROJECT = "project";
  public static final String MUTATION_TRANSCRIPTS = "transcript";
  public static final String MUTATION_TRANSCRIPTS_GENE = "gene";
  public static final String MUTATION_TRANSCRIPTS_CONSEQUENCE = "consequence";

  // TODO: Move to summary
  public static final String MUTATION_CONSEQUENCE_TYPES = "consequence_type";
  public static final String MUTATION_FUNCTIONAL_IMPACT_PREDICTION_SUMMARY = "functional_impact_prediction_summary";
  public static final String MUTATION_TRANSCRIPTS_FUNCTIONAL_IMPACT_PREDICTION_SUMMARY =
      "functional_impact_prediction_summary";
  public static final String MUTATION_TRANSCRIPTS_FUNCTIONAL_IMPACT_PREDICTION = "functional_impact_prediction";
  public static final String MUTATION_PLATFORM = "platform";
  public static final String MUTATION_IS_ANNOTATED = "is_annotated";
  public static final String MUTATION_VALIDATION_STATUS = "validation_status";
  public static final String MUTATION_VERIFICATION_STATUS = "verification_status";
  public static final String MUTATION_SEQUENCING_STRATEGY = "sequencing_strategy";

  public static final String MUTATION_SUMMARY = "_summary";
  public static final String MUTATION_SUMMARY_AFFECTED_PROJECT_IDS = "_affected_project_id";
  public static final String MUTATION_SUMMARY_AFFECTED_PROJECT_COUNT = "_affected_project_count";
  public static final String MUTATION_SUMMARY_AFFECTED_DONOR_COUNT = "_affected_donor_count";
  public static final String MUTATION_SUMMARY_TESTED_DONOR_COUNT = "_tested_donor_count";

  /**
   * Release field names.
   */
  public static final String RELEASE_ID = "_release_id";
  public static final String RELEASE_NAME = "name";
  public static final String RELEASE_NUMBER = "number";
  public static final String RELEASE_DATE = "date";
  public static final String RELEASE_PROJECT_COUNT = "project_count";
  public static final String RELEASE_LIVE_PROJECT_COUNT = "live_project_count";
  public static final String RELEASE_PRIMARY_SITE_COUNT = "primary_site_count";
  public static final String RELEASE_LIVE_PRIMARY_SITE_COUNT = "live_primary_site_count";
  public static final String RELEASE_DONOR_COUNT = "donor_count";
  public static final String RELEASE_LIVE_DONOR_COUNT = "live_donor_count";
  public static final String RELEASE_SPECIMEN_COUNT = "specimen_count";
  public static final String RELEASE_SAMPLE_COUNT = "sample_count";
  public static final String RELEASE_SSM_COUNT = "ssm_count";
  public static final String RELEASE_MUTATED_GENE_COUNT = "mutated_gene_count";

  /**
   * Reactome pathway names.
   */
  public static final String PATHWAY_REACTOME_ID = "pathway_id";
  public static final String PATHWAY_NAME = "pathway_name";
  public static final String PATHWAY_EVIDENCE_CODE = "evidence_code";
  public static final String PATHWAY_UNIPROT_ID = "uniprot_id";
  public static final String PATHWAY_URL = "url";
  public static final String PATHWAY_SPECIES = "species";
  public static final String PATHWAY_GENE_COUNT = "gene_count";
  public static final String PATHWAY_PARENT_PATHWAYS = "parent_pathways";
  public static final String PATHWAY_HAS_DIAGRAM = "has_diagram";
  public static final String PATHWAY_SUMMATION = "summation";
  public static final String PATHWAY_SOURCE = "source";

  /**
   * Reactome diagram field names
   */
  public static final String DIAGRAM_ID = "diagram_id";
  public static final String DIAGRAM_XML = "xml";
  public static final String DIAGRAM_HIGHLIGHTS = "highlights";
  public static final String DIAGRAM_PROTEIN_MAP = "protein_map";
  public static final String DIAGRAM_PROTEIN_MAP_DB_ID = "db_id";
  public static final String DIAGRAM_PROTEIN_MAP_UNIPROT_IDS = "uniprot_ids";

  /**
   * Drug field names
   */
  public static final String DRUG_ID = "zinc_id";
  public static final String NAME = "name";
  public static final String IMG_URL = "image_url";
  public static final String INCHIKEY = "inchikey";
  public static final String SYNONYMS = "synonyms";
  public static final String DRUG_CLASS = "drug_class";
  public static final String ATC_CODES = "atc_codes";
  public static final String EXTERNAL_REFERENCES = "external_references";
  public static final String GENES = "genes";
  public static final String CANCER_TRIAL_COUNT = "cancer_trial_count";
  public static final String TRIALS = "trials";

  /**
   * File field names
   */
  public static final String FILE_ID = "file_id";

  /**
   * Gene set field names.
   */
  public static final String GENE_SET_ID = "id";
  public static final String GENE_SET_TYPE = "type";
  public static final String GENE_SET_NAME = "name";
  public static final String GENE_SET_SOURCE = "source";
  public static final String GENE_SET_DESCRIPTION = "description";
  public static final String GENE_SET_PROJECTS = "projects";
  public static final String GENE_SET_PATHWAY = "pathway";
  public static final String GENE_SET_GO_TERM = "go_term";
  public static final String GENE_SET_CURATED_SET = "curated_set";

  /**
   * Aggregate field names.
   */
  public static final String SYNTHETIC_PREFIX = "_";
  public static final String TYPE_COUNT_SUFFIX = "_count";
  public static final String TYPE_EXISTS_SUFFIX = "_exists";
  public static final String AVAILABLE_DATA_TYPES = "_available_data_type";
  public static final String DONOR_SAMPLE_STUDY = "study";
  public static final String DONOR_SUMMARY_STUDIES = "_studies";
  public static final String TOTAL_DONOR_COUNT = "_total_donor_count";
  public static final String TOTAL_LIVE_DONOR_COUNT = "_total_live_donor_count";
  public static final String TOTAL_SPECIMEN_COUNT = "_total_specimen_count";
  public static final String TOTAL_SAMPLE_COUNT = "_total_sample_count";
  public static final String TESTED_DONOR_COUNT_SUFFIX = "_tested_donor_count";
  public static final String AFFECTED_DONOR_COUNT = "_affected_donor_count";
  public static final String EXPERIMENTAL_ANALYSIS_PERFORMED_DONOR_COUNT =
      "experimental_analysis_performed_donor_count";
  public static final String EXPERIMENTAL_ANALYSIS_PERFORMED_SAMPLE_COUNT =
      "experimental_analysis_performed_sample_count";
  public static final String AVAILABLE_EXPERIMENTAL_ANALYSIS_PERFORMED =
      "available_experimental_analysis_performed";

  public static String getTestedTypeCountFieldName(FeatureType featureType) {
    return SYNTHETIC_PREFIX + featureType.getId() + TESTED_DONOR_COUNT_SUFFIX;
  }

}
